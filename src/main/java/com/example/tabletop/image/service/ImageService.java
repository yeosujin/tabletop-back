package com.example.tabletop.image.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.exception.ImageNotFoundException;
import com.example.tabletop.image.exception.ImageProcessingException;
import com.example.tabletop.image.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
	@Value("${store.files.save.dir}")
	String saveDir;
	
	@Value("${cloud.aws.s3.bucket}")	
  	private final String bucketName;
	
	private final String S3_NAME = "tabletop";
	private String STORE_DIR_NAME = null;
	
	private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Transactional
    public Image saveImage(MultipartFile file, Long parentId, ImageParentType parentType) throws ImageProcessingException, Exception {
    	if(file == null) {
			throw new Exception("파일 전달 오류 발생");
		}
		
		if(parentType == ImageParentType.STORE) {
			STORE_DIR_NAME = parentId.toString();
		}
			
        try {
        	log.info("Saving image entity");
        	
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            Path filepath = Paths.get(saveDir, filename);
            Files.createDirectories(filepath.getParent());
            Files.write(filepath, file.getBytes());
            
            Image imageEntity = Image.builder()
					.parentId(parentId)
					.parentType(parentType)
					.filename(filename)
					.fileOriginalName(file.getOriginalFilename())
					.build();
            
            try {
        		log.info("Saving image to S3");
        		
        		if(imageEntity.getImageId() != null) {
    				File uploadFile = new File(imageEntity.getFilepath() + "\\" + imageEntity.getFilename());
    				file.transferTo(uploadFile);
    				
    				amazonS3.putObject(new PutObjectRequest(bucketName, S3_NAME + File.separator + STORE_DIR_NAME + File.separator + uploadFile.getName(), uploadFile)
                          .withCannedAcl(CannedAccessControlList.PublicRead));
    				
    				if(uploadFile.exists()) {
    					uploadFile.delete();
    				}
    				
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
            
            return imageRepository.save(imageEntity);
        } catch (IOException e) {
            log.error("Failed to save image", e);
            throw new ImageProcessingException("Failed to save image: " + e.getMessage());
        }
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
    public void deleteImage(Long imageId) throws ImageProcessingException {
        try {
            Image image = getImage(imageId);
            Path filepath = Paths.get(image.getFilepath());
            Files.deleteIfExists(filepath);
            imageRepository.delete(image);
            log.info("Deleted image: {}", image.getFilename());
        } catch (IOException e) {
            log.error("Failed to delete image", e);
            throw new ImageProcessingException("Failed to delete image: " + e.getMessage());
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