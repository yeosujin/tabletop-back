package com.example.tabletop.store.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.store.service.StoreService;

@RestController
public class StoreController {

	@Value("${store.files.save.path}")
	String savePath;
	
	private final StoreService storeService;
//	private final FileService fileService;
	
	// 가게 목록 조회
	@GetMapping("api/stores/{username}")
	public List<Store> getStoresByUsername(@PathVariable String username) {
		List<Store> stores = storeService.getStoresByUsername(username);
		String storeDir = savePath + Image.separator + targetProject.getProjectId().toString();
		Image directory = new Image(storeDir);
		
	}
	
	// 파일 업로드
	@PostMapping("/api/project/{projectId}/files")
	public void uploadFiles(@RequestParam("files") List<MultipartFile> files, 
							@RequestParam("paths") List<String> paths, 
							@PathVariable Long projectId) {
		
		// 업로드 대상 프로젝트
		Project targetProject = projectService.getProjectByProjectId(projectId);
		
		for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String path = paths.get(i);
            
            String originalFilename = file.getOriginalFilename();
	        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
	        
			try {
				String projectDir = savePath + Image.separator + projectId.toString();
				System.out.println("projectDir:" + projectDir);
				Image directory = new Image(projectDir);
				
				// 프로젝트 디렉토리가 생성되어 있지 않으면 디렉토리 생성
				if(!directory.exists()) {
					boolean result = directory.mkdir();
					directory.mkdir();
				}
				
				String subDirectory = extractSubDirectory(path);
				System.out.println("subDirectory:" + subDirectory);
	            if (!subDirectory.isEmpty()) {
	                directory = new Image(projectDir + Image.separator + subDirectory);
	                directory.mkdirs();
	            }
				
				// 서버로 파일 업로드
	            Image destFile = new Image(directory, uniqueFilename);
	            file.transferTo(destFile);
	            
	            
				ProjectfileDTO projectfileDTO = ProjectfileDTO.builder()
												.filePath(destFile.getParent())
												.fileName(uniqueFilename)
												.fileOriginalName(originalFilename)
												.fileSize(file.getSize())
												.project(targetProject)
												.build();
				
				projectfileService.insertProjectfile(projectfileDTO);
				
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 프로젝트 폴더와 최종 업로드 파일 사이 경로 추출
	private String extractSubDirectory(String fullPath) {
	    if (fullPath == null) return "";
	    int lastIndex = fullPath.lastIndexOf("/");
	    return lastIndex < 0 ? "" : fullPath.substring(1, lastIndex);
	}
		
	// 파일 다운로드
	@GetMapping("/api/project/{userName}/{projectTitle}/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
		/*
		 * -- logic --
		 * 1) 물리적인 파일 선택(경로)
		 * 2) 리소스화(inputStream)
		 * 3) return(header)
		 */
		
		Resource resource = null;
		ProjectfileDTO targetProjectfile = projectfileService.getProjectfileByFileId(fileId);
		String fullPath = targetProjectfile.getFilePath() + Image.separator + targetProjectfile.getFileName();
		Path path = Paths.get(fullPath);
		
		try {
			resource = new InputStreamResource(Files.newInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		// Content-Disposition: 전송되는 리소스의 처리 방법 지정(다운로드 파일 설정)
		headers.setContentDisposition(ContentDisposition
										.builder("attachment")
										.filename(targetProjectfile.getFileOriginalName())
										.build());
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
	
	// 파일 삭제
	@DeleteMapping("/api/project/{projectId}/{fileId}")
	public void deleteFile(@PathVariable Long projectId, @PathVariable Long fileId) {
		projectfileService.deleteProjectfileByFileId(fileId);
	}
}
