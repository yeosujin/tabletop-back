package com.example.tabletop.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final SellerRepository sellerRepository;
	
	@Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Seller seller = sellerRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("loginId not found : " + loginId));
        
        return new CustomUserDetails(seller);
    }
	
}
