package com.example.tabletop.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.exception.SellerNotFoundException;
import com.example.tabletop.seller.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	private final SellerRepository sellerRepository;
	
	@Override
    public UserDetails loadUserByUsername(String loginId) throws SellerNotFoundException {
        Seller seller = sellerRepository.findByLoginId(loginId)
                .orElseThrow(() -> new SellerNotFoundException("해당 로그인 ID가 존재하지 않습니다."));
        
        return new CustomUserDetails(seller);
    }	
}