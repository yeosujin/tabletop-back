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
public class SellerResponseDTO {
    private String loginId;
    private String username;
    private String password;
    private String email;    
    private String mobile;
    private Boolean doneClickCountSetting;

    public static SellerResponseDTO toDTO(Seller seller) {
        return SellerResponseDTO.builder()
                .loginId(seller.getLoginId())
                .username(seller.getUsername())
                .email(seller.getEmail())
                .mobile(seller.getMobile())
                .doneClickCountSetting(seller.getDoneClickCountSetting())
                .build();
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class SellerInfoDTO {
        private String loginId;
        private String username;
        private String email;
        private String mobile;
        private Boolean doneClickCountSetting;

        public static SellerInfoDTO toDTO(Seller seller) {
            return SellerInfoDTO.builder()
                    .loginId(seller.getLoginId())
                    .username(seller.getUsername())
                    .email(seller.getEmail())
                    .mobile(seller.getMobile())
                    .doneClickCountSetting(seller.getDoneClickCountSetting())
                    .build();
        }
    }   
    
}
