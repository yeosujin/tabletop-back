package com.example.tabletop.seller.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabletop.seller.dto.SellerDTO;
import com.example.tabletop.seller.dto.SellerResponseDTO;
import com.example.tabletop.seller.exception.DuplicateLoginIdException;
import com.example.tabletop.seller.exception.InvalidSellerDataException;
import com.example.tabletop.seller.exception.SellerNotFoundException;
import com.example.tabletop.seller.service.SellerService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/sellers")
@RestController
public class SellerController {
	
	private final SellerService sellerService;
	
	// 회원가입
	@PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SellerDTO sellerDto) {
        sellerService.signUp(sellerDto);
        return new ResponseEntity<>("회원가입이 성공적으로 완료되었습니다.", HttpStatus.CREATED);
    }
	
	// 아이디 중복 체크
	@GetMapping("/exists")
    public ResponseEntity<String> checkLoginId(@RequestParam String loginId) throws DuplicateLoginIdException {
		sellerService.isLoginIdDuplicate(loginId);
        return new ResponseEntity<>("사용 가능한 ID입니다.", HttpStatus.OK);
    }
		
	// 판매자 정보 조회
    @GetMapping("/{loginId}")
    public ResponseEntity<SellerResponseDTO.SellerInfoDTO> getSeller(@PathVariable String loginId) throws SellerNotFoundException {
        SellerResponseDTO.SellerInfoDTO sellerDto = sellerService.getSeller(loginId);
        return new ResponseEntity<>(sellerDto, HttpStatus.OK);
    }
	
	// 판매자 정보 수정
	@PutMapping("/{loginId}")
    public ResponseEntity<SellerResponseDTO> updateSeller(@PathVariable String loginId, @RequestBody SellerDTO sellerDto) throws SellerNotFoundException, InvalidSellerDataException {
        System.out.println(sellerDto);
        SellerResponseDTO updatedSeller = sellerService.updateSeller(loginId, sellerDto);
        return new ResponseEntity<>(updatedSeller, HttpStatus.OK);
    }
		
	@DeleteMapping("/{loginId}")
    public ResponseEntity<String> deleteSeller(@PathVariable String loginId) throws SellerNotFoundException {
        sellerService.deleteSeller(loginId);
        return new ResponseEntity<>("판매자 계정이 성공적으로 삭제되었습니다.", HttpStatus.NO_CONTENT);        
    }

    // get doneClickCountSetting
    @GetMapping("/{loginId}/count-setting")
    public ResponseEntity<Boolean> readCountSetting(@PathVariable String loginId) throws SellerNotFoundException {
        return ResponseEntity.ok(sellerService.readClickCountSetting(loginId));
    }

}