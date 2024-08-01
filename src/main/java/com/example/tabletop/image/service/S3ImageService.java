package com.example.tabletop.image.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.repository.ImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class S3ImageService {
	
	private final AmazonS3 amazonS3;
	private final ImageRepository ImageRepository;
	
  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;
   private final String DIR_NAME = "s3_dir";
 	@Transactional
	public void uploadS3File(MultipartFile file, long parentId) throws Exception {
		
		if(file == null) {
			throw new Exception("파일 전달 오류 발생");
		}
		
		List<Image> fileList = new ArrayList<>();
		
			String attachmentOriginalFileName = file.getOriginalFilename();
			UUID uuid = UUID.randomUUID();
			String ImageName = uuid.toString() + "_" + attachmentOriginalFileName;
			Long ImageSize = file.getSize();
			
			Image image = Image.builder()
					.parentId(parentId)
					.parentType(ImageParentType.MENU)
					.filename(ImageName)
					.fileOriginalName(attachmentOriginalFileName)
					.filepath("C:\\tabletop")
					.build();
			
			fileList.add(image);
			
			Long fileNo = ImageRepository.save(image).getImageId();
			
			if(fileNo != null) {
				File uploadFile = new File(image.getFilepath() + "\\" + image.getFilename());
				file.transferTo(uploadFile);
				
				amazonS3.putObject(new PutObjectRequest(bucketName, DIR_NAME + "/" + uploadFile.getName(), uploadFile)
                      .withCannedAcl(CannedAccessControlList.PublicRead));
				
				if(uploadFile.exists()) {
					uploadFile.delete();
				}
			}
	}
	
	@Transactional
	public ResponseEntity<Resource> downloadS3File(long fileNo){
		Image Image = null;
		Resource resource = null;
		try {
			Image = ImageRepository.findById(fileNo)
												.orElseThrow(() -> new NoSuchElementException("파일 없음"));
			
	        S3Object awsS3Object = amazonS3.getObject(new GetObjectRequest(bucketName, DIR_NAME + "/" + Image.getFilename()));
	        S3ObjectInputStream s3is = awsS3Object.getObjectContent();
			
			resource = new InputStreamResource(s3is);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Resource>(resource, null, HttpStatus.NO_CONTENT);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDisposition(ContentDisposition
											.builder("attachment")
											.filename(Image.getFileOriginalName())
											.build());
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
	
}



