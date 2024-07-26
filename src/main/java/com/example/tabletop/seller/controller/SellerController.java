package com.example.tabletop.seller.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabletop.seller.dto.SellerDTO;
import com.example.tabletop.seller.service.SellerService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/sellers")
@RestController
public class SellerController {
	
	private final SellerService sellerService;
	
	@PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SellerDTO sellerDto) {
        sellerService.signUp(sellerDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}