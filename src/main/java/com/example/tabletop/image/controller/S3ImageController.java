package com.example.tabletop.image.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.image.service.S3ImageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class S3ImageController {
	
	private final S3ImageService s3Service;
	
	@PostMapping(value = "/api/s3/file")
	public void uploadS3File(MultipartFile file) {
		try {
			s3Service.uploadS3File(file, 30L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping(value = "/api/s3/files/{fileNo}")
	public ResponseEntity<Resource> downloadS3File(@PathVariable long fileNo) throws Exception {
		return s3Service.downloadS3File(fileNo);
	}
}
