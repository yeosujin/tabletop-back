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
    
    public Seller toEntity() {
        return Seller.builder()
                .loginId(this.loginId)
                .username(this.username)
                .password(this.password)
                .email(this.email)
                .mobile(this.mobile)
                .refreshToken(this.refreshToken)
                .doneClickCountSetting(this.doneClickCountSetting)
                .build();
    }
}