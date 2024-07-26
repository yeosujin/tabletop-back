package com.example.tabletop.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.tabletop.auth.dto.LoginRequestDTO;
import com.example.tabletop.auth.dto.LoginResponseDTO;
import com.example.tabletop.commons.config.JwtTokenProvider;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;
    private final SellerRepository sellerRepository;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword()));
    	
        Seller seller = ((CustomUserDetails) authentication.getPrincipal()).getSeller();
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken();
        
        seller.setRefreshToken(refreshToken);
        sellerRepository.save(seller);

        return new LoginResponseDTO(accessToken);
    }
}