package com.example.tabletop.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.store.dto.StoreListResponseDTO;
import com.example.tabletop.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreController {

	@Value("${store.files.save.path}")
	String savePath;
	
	private final StoreService storeService;
	
	// 로그인한 판매자의 가게 목록 조회
	@GetMapping("api/stores/{loginId}")
	public ResponseEntity<?> getStoreListByUsername(@PathVariable String loginId) {
		List<StoreListResponseDTO> storeList = storeService.getStoreListByLoginId(loginId);

		try {
			return new ResponseEntity<List<StoreListResponseDTO>>(storeList, HttpStatus.OK);			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");  // HTTP 500 Internal Server Error
		}
	}

	// 사업자등록번호 중복 확인
	@GetMapping("/api/dupalicationCheck/{corporateRegistrationNumber}")
	public ResponseEntity<Map<String, String>> checkCorporateRegistrationNumberDuplication(@PathVariable String corporateRegistrationNumber) {

		Map<String, String> result = new HashMap<>();
		
		if(storeService.checkCorporateRegistrationNumberDuplication(corporateRegistrationNumber)) {
			result.put("isDuplicated", "true");
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			result.put("isDuplicated", "false");
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
	}
	
	// 이 부분부터 다시 post main 테스트 하기
	// 가게 등록
	@PostMapping("api/store/{login_id}")
	public void insertStore(@PathVariable String loginId,
							@RequestParam String name,
							@RequestParam String storeType,
							@RequestParam(required = false) String corporateRegistrationNumber,
							@RequestParam(required = false) String openDate,
							@RequestParam(required = false) String closeDate,
				            @RequestParam String description,
				            @RequestParam String address,
				            @RequestParam String notice,
				            @RequestParam String openTime,
				            @RequestParam String closeTime,
				            @RequestParam String holidays,
				            @RequestParam(required = false) MultipartFile image) {
		
		storeService.insertStore(loginId, name, storeType, corporateRegistrationNumber, openDate, closeDate,
	            					description, address, notice, openTime, closeTime, holidays, image);
		
	}
	
	// 가게 수정
	@PutMapping("api/stores/{store_id}")
	public void updateStoreByStoreId(@PathVariable Long storeId,
									@RequestParam String name,
						            @RequestParam String description,
						            @RequestParam String address,
						            @RequestParam String notice,
						            @RequestParam String openTime,
						            @RequestParam String closeTime,
						            @RequestParam String holidays,
						            @RequestParam(required = false) MultipartFile image) {
		
		storeService.updateStoreByStoreId(storeId, name, description, address, notice, openTime, closeTime, holidays, image);
		
	}
	
	// 가게 삭제
	@DeleteMapping("api/stores/{store_id}")
    public ResponseEntity<?> deleteStoreByStoreId(@PathVariable Long store_id, @RequestParam String password) {
        try {
        	storeService.deleteStoreByStoreId(store_id, password);
            return ResponseEntity.ok().build();  // HTTP 200 OK
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid password");  // HTTP 403 Forbidden
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");  // HTTP 500 Internal Server Error
        }
        
    }
	
}
