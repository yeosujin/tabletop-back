package com.example.tabletop.store.controller;

<<<<<<< Updated upstream
import java.util.List;
=======
import java.util.HashMap;
import java.util.List;
import java.util.Map;
>>>>>>> Stashed changes

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabletop.store.dto.StoreListResponseDTO;
import com.example.tabletop.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreController {

	@Value("${store.files.save.path}")
	String savePath;
	
	private final StoreService storeService;
	
	// 가게 목록 조회
	@GetMapping("api/stores/{login_id}")
<<<<<<< Updated upstream
	public ResponseEntity<?> getStoreListByUsername(@PathVariable String login_id) {
		List<StoreListResponseDTO> storeList = storeService.getStoreListByLoginId(login_id);
=======
	public ResponseEntity<?> getStoreListByUsername(@PathVariable String loginId) {
		List<StoreListResponseDTO> storeList = storeService.getStoreListByLoginId(loginId);
>>>>>>> Stashed changes
		
		try {
			return new ResponseEntity<List<StoreListResponseDTO>>(storeList, HttpStatus.OK);			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");  // HTTP 500 Internal Server Error
		}
	}
	
<<<<<<< Updated upstream
=======
	// 사업자등록번호 중복 확인
	@GetMapping("/api/store/{corporate_registration_number}")
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
	
>>>>>>> Stashed changes
	// 가게 삭제
	@DeleteMapping("api/stores/{store_id}")
    public ResponseEntity<?> deleteStoreByStoreId(@PathVariable Long storeId, @RequestParam String password) {
        
        try {
        	storeService.deleteStoreByStoreId(storeId, password);
            return ResponseEntity.ok().build();  // HTTP 200 OK
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid password");  // HTTP 403 Forbidden
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");  // HTTP 500 Internal Server Error
        }
        
    }
	
}