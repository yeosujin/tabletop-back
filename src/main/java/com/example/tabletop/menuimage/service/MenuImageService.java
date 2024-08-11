package com.example.tabletop.menuimage.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menuimage.entity.MenuImage;
import com.example.tabletop.menuimage.exception.MenuImageProcessingException;
import com.example.tabletop.menuimage.exception.MenuImageNotFoundException;
import com.example.tabletop.menuimage.repository.MenuImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuImageService {
	@Value("${store.files.save.dir}")
	String saveDir;
	
	@Value("${cloud.aws.s3.bucket}")	
  	String bucketName;
	
	private final String S3_NAME = "tabletop/menu";
	private String MENU_DIR_NAME = null;
	
	private final AmazonS3 amazonS3;
    private final MenuImageRepository menuImageRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public MenuImage saveImage(MultipartFile file, Long menuId) throws MenuImageProcessingException, Exception {
    	if(file == null) {
			throw new Exception("파일 전달 오류 발생");
		}
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("Menu not found"));
        MENU_DIR_NAME = menu.getId().toString();
			
        try {
        	log.info("Saving image entity");
        	
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        
            String s3Key = S3_NAME + "/" + MENU_DIR_NAME + "/" + filename;
            
            MenuImage imageEntity = MenuImage.builder()
					.menuId(menuId)
					.filename(filename)
					.fileOriginalName(file.getOriginalFilename())
					.filepath(saveDir)
					.S3Url("https://tabletop-tabletop.s3.ap-northeast-2.amazonaws.com/" + S3_NAME + "/" + MENU_DIR_NAME + "/" + filename) 
					.build();

            Long savedImageId = menuImageRepository.save(imageEntity).getMenuImageId();
    		if(savedImageId != null) {
    			log.info("Saving image to S3");
    			
    			menu.setMenuImage(imageEntity);
    			
				File uploadFile = new File(imageEntity.getFilepath() + "\\" + imageEntity.getFilename());
				file.transferTo(uploadFile);
				
				amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, uploadFile)
                      .withCannedAcl(CannedAccessControlList.PublicRead));
				
				if(uploadFile.exists()) {
					uploadFile.delete();
				}
    		}
    			
            return imageEntity;
        } catch (IOException e) {
            log.error("Failed to save image", e);
            throw new MenuImageProcessingException("Failed to save image: " + e.getMessage());
        } catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
    }

    @Transactional(readOnly = true)
    public MenuImage getImage(Long imageId) {
        log.info("Fetching image with id: {}", imageId);
        return menuImageRepository.findById(imageId)
                .orElseThrow(() -> {
                    log.error("MenuImage not found with id: {}", imageId);
                    return new MenuImageNotFoundException("MenuImage not found with id: " + imageId);
                });
    }

    @Transactional(readOnly = true)
    public List<MenuImage> getImagesByMenuId(Long menuId) {
        log.info("Fetching images for parent id: {}", menuId);
        return menuImageRepository.findByMenuId(menuId);
    }

    @Transactional
    public void deleteImage(Long imageId) throws MenuImageProcessingException {
        try {
            MenuImage image = getImage(imageId);
            // S3 에서 삭제하는 로직으로 변경
//            Path filepath = Paths.get(image.getFilepath());
//            Files.deleteIfExists(filepath);
            
            // fileName : 폴더명/파일네임.확장자
            String fileName = "tabletop/menu/" + image.getMenuId() + "/" +  image.getFilename();
            amazonS3.deleteObject(bucketName, fileName);
            
            menuImageRepository.delete(image);
            log.info("Deleted image: {}", image.getFilename());
        } catch (SdkClientException e) {
            log.error("Failed to delete image", e);
            throw new MenuImageProcessingException("Failed to delete image: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public byte[] getImageBytes(Long imageId) throws MenuImageProcessingException {
        try {
            MenuImage image = getImage(imageId);
            Path filepath = Paths.get(image.getFilepath());
            log.info("Reading bytes for image: {}", image.getFilename());
            return Files.readAllBytes(filepath);
        } catch (IOException e) {
            log.error("Failed to read image bytes", e);
            throw new MenuImageProcessingException("Failed to read image bytes: " + e.getMessage());
        }
    }
}