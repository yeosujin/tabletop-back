package com.example.tabletop.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.store.dto.StoreDetailsDTO;
import com.example.tabletop.store.dto.StoreListResponseDTO;
import com.example.tabletop.store.dto.StoreRequestDTO;
import com.example.tabletop.store.service.StoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}

	// 사업자등록번호 중복 확인
	@GetMapping("api/duplicationCheck/{corporateRegistrationNumber}")
	public ResponseEntity<Map<String, String>> checkCorporateRegistrationNumberDuplication(@PathVariable String corporateRegistrationNumber) {

		Map<String, String> result = new HashMap<>();
		
		if(storeService.checkCorporateRegistrationNumberDuplication(corporateRegistrationNumber)) {
			result.put("isDuplicated", "true");
		} else {
			result.put("isDuplicated", "false");
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	// 가게 등록
	@PostMapping("api/store/{loginId}")
	public ResponseEntity<String> insertStore(@PathVariable String loginId,
												@RequestPart("storeData") StoreRequestDTO storeRequest,
												@RequestPart(required = false) MultipartFile image) {
		try {
			storeService.insertStore(loginId,
									storeRequest.getName(),
									storeRequest.getStoreType(),
									storeRequest.getCorporateRegistrationNumber(),
									storeRequest.getOpenDate(),
									storeRequest.getCloseDate(),
									storeRequest.getDescription(),
									storeRequest.getAddress(),
									storeRequest.getNotice(),
									storeRequest.getOpenTime(),
									storeRequest.getCloseTime(),
									storeRequest.getHolidays(),
									image);

			return ResponseEntity.status(HttpStatus.OK).body("등록 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
		
	}

	// 가게 상세 정보 조회
	@GetMapping("api/stores/{storeId}/details")
	public ResponseEntity<?> getStoreDetails(@PathVariable Long storeId) {
		try {
			StoreDetailsDTO storeDetails = storeService.getStoreDetails(storeId);
			return new ResponseEntity<StoreDetailsDTO>(storeDetails, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching store details");
		}
	}
	
	// 가게 수정
	@PutMapping("api/stores/{storeId}")
	public ResponseEntity<String> updateStoreByStoreId(@PathVariable Long storeId,
									@RequestPart("storeData") StoreRequestDTO storeRequest,
									@RequestPart(required = false) MultipartFile image) {
		
		try {
			storeService.updateStoreByStoreId(storeId,
											storeRequest.getName(),
											storeRequest.getDescription(),
											storeRequest.getAddress(),
											storeRequest.getNotice(),
											storeRequest.getOpenTime(),
											storeRequest.getCloseTime(),
											storeRequest.getHolidays(),
											image);

			return ResponseEntity.status(HttpStatus.OK).body("수정 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
		}
	}
	
	// 가게 삭제
	@DeleteMapping("api/stores/{store_id}")
    public ResponseEntity<?> deleteStoreByStoreId(@PathVariable Long store_id, @RequestBody String password) {
        try {
        	storeService.deleteStoreByStoreId(store_id, password);
            return ResponseEntity.ok().build();
//        } catch (AccessDeniedException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid password");  // HTTP 403 Forbidden
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
        
    }
	
}
