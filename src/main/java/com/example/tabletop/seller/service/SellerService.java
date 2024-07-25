package com.example.tabletop.seller.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.tabletop.seller.dto.SellerDTO;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SellerService {
	
    private final PasswordEncoder passwordEncoder;
    private final SellerRepository sellerRepository;

    public void signUp(SellerDTO sellerDto) {
        sellerDto.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
        sellerDto.setDoneClickCountSetting(false);
        
        Seller seller = sellerDto.toEntity();
        sellerRepository.save(seller);
    }

}
