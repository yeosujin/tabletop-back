package com.example.tabletop.seller.dto;

import com.example.tabletop.seller.entity.Seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SellerDTO {
    private String loginId;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String refreshToken;
    private Boolean doneClickCountSetting;
    
    public static Seller toEntity(SellerDTO sellerDTO) {
        return Seller.builder()
                .loginId(sellerDTO.getLoginId())
                .username(sellerDTO.getUsername())
                .password(sellerDTO.getPassword())
                .email(sellerDTO.getEmail())
                .mobile(sellerDTO.getMobile())
                .refreshToken(sellerDTO.getRefreshToken())
                .doneClickCountSetting(sellerDTO.getDoneClickCountSetting())
                .build();
    }
}