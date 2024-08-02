package com.example.tabletop.image.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import java.util.NoSuchElementException;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.exception.ImageNotFoundException;
import com.example.tabletop.image.exception.ImageProcessingException;
import com.example.tabletop.image.repository.ImageRepository;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
	@Value("${store.files.save.dir}")
	String saveDir;
	
	@Value("${cloud.aws.s3.bucket}")	
  	String bucketName;
	
	private final String S3_NAME = "tabletop";
	private String STORE_DIR_NAME = null;
	
	private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public Image saveImage(MultipartFile file, Long parentId, ImageParentType parentType) throws Exception {
    	if(file == null) {
			throw new Exception("파일 전달 오류 발생");
		}
		if(parentType == ImageParentType.STORE) {
			STORE_DIR_NAME = parentId.toString();
		} else if (parentType == ImageParentType.MENU) {
	        Menu menu = menuRepository.findById(parentId)
	                .orElseThrow(() -> new NoSuchElementException("Menu not found"));
	        STORE_DIR_NAME = menu.getStore().getStoreId().toString();
	    }
			
        try {
        	log.info("Saving image entity");
        	
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        
            Image imageEntity = Image.builder()
					.parentId(parentId)
					.parentType(parentType)
					.filename(filename)
					.fileOriginalName(file.getOriginalFilename())
					.filepath("C:\\tabletop")
					.S3Url(STORE_DIR_NAME + "/" + filename) 
					.build();

            Image savedImageEntity = imageRepository.save(imageEntity);
    		if(savedImageEntity.getImageId() != null) {
    			log.info("Saving image to S3");
				File uploadFile = new File(imageEntity.getFilepath() + "\\" + imageEntity.getFilename());
				file.transferTo(uploadFile);
				
				amazonS3.putObject(new PutObjectRequest(bucketName, S3_NAME + "/" + STORE_DIR_NAME + "/" + uploadFile.getName(), uploadFile)
                      .withCannedAcl(CannedAccessControlList.PublicRead));
				
				if(uploadFile.exists()) {
					uploadFile.delete();
				}
    		}
    			
            return savedImageEntity;
        } catch (IOException e) {
            log.error("Failed to save image", e);
            throw new ImageProcessingException("Failed to save image: " + e.getMessage());
        } catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
    }

    @Transactional(readOnly = true)
    public Image getImage(Long imageId) {
        log.info("Fetching image with id: {}", imageId);
        return imageRepository.findById(imageId)
                .orElseThrow(() -> {
                    log.error("Image not found with id: {}", imageId);
                    return new ImageNotFoundException("Image not found with id: " + imageId);
                });
    }

    @Transactional(readOnly = true)
    public List<Image> getImagesByParent(Long parentId, ImageParentType parentType) {
        log.info("Fetching images for parent id: {} and type: {}", parentId, parentType);
        return imageRepository.findByParentIdAndParentType(parentId, parentType);
    }

    @Transactional
    public void deleteFolderFromS3(Long storeId) throws Exception {
    	String folderKey = S3_NAME + "/" + storeId.toString();

        try {
            // 1. 리스트 요청을 통해 폴더 내부의 객체 목록을 가져옴
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(folderKey);

            List<S3ObjectSummary> objectSummaries = new ArrayList<>();
            ListObjectsV2Result result;

            do {
                result = amazonS3.listObjectsV2(listObjectsV2Request);
                objectSummaries.addAll(result.getObjectSummaries());
                listObjectsV2Request.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());

            // 2. 객체가 없으면 바로 리턴
            if (objectSummaries.isEmpty()) {
                return;
            }

            // 3. 삭제 요청 객체를 만듦
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(objectSummaries.stream().map(S3ObjectSummary::getKey).toArray(String[]::new));

            // 4. 객체 삭제
            DeleteObjectsResult deleteObjectsResult = amazonS3.deleteObjects(deleteObjectsRequest);
            int deletedCount = deleteObjectsResult.getDeletedObjects().size();
            log.info("Deleted " + deletedCount + " objects from S3 folder: " + folderKey);
        } catch (AmazonServiceException e) {
            log.error("Failed to delete S3 folder", e);
            throw new Exception("Failed to delete S3 folder: " + e.getMessage());
        } catch (SdkClientException e) {
            log.error("Failed to delete S3 folder", e);
            throw new Exception("Failed to delete S3 folder: " + e.getMessage());
        }
    }
    
    @Transactional
    public void deleteImageFromS3(Long imageId) throws Exception {
    	try {
            // 1. 데이터베이스에서 이미지 엔티티 조회
            Optional<Image> imageEntityOptional = imageRepository.findById(imageId);
            if (!imageEntityOptional.isPresent()) {
                throw new Exception("이미지를 찾을 수 없습니다.");
            }
            Image imageEntity = imageEntityOptional.get();

            // 2. S3에서 이미지 삭제
            String s3Key = S3_NAME + "/" + imageEntity.getS3Url();
            log.info("Deleting image from S3: " + s3Key);
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, s3Key));

            // 3. 데이터베이스에서 이미지 엔티티 삭제
            log.info("Deleting image entity from database");
            imageRepository.delete(imageEntity);
        } catch (AmazonServiceException e) {
            log.error("Failed to delete image from S3", e);
            throw new Exception("Failed to delete image from S3: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete image", e);
            throw new Exception("Failed to delete image: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public byte[] getImageBytes(Long imageId) throws ImageProcessingException {
        try {
            Image image = getImage(imageId);
            Path filepath = Paths.get(image.getFilepath());
            log.info("Reading bytes for image: {}", image.getFilename());
            return Files.readAllBytes(filepath);
        } catch (IOException e) {
            log.error("Failed to read image bytes", e);
            throw new ImageProcessingException("Failed to read image bytes: " + e.getMessage());
        }
    }
}