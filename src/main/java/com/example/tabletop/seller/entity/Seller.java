package com.example.tabletop.seller.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.tabletop.seller.dto.SellerDTO;
import com.example.tabletop.store.entity.Store;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "seller")
@DynamicUpdate
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long id;

    @Column(name = "login_id", nullable = false, length = 30, unique = true)
    private String loginId;

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "mobile", length = 14)
    private String mobile;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "done_click_count_setting")
    private Boolean doneClickCountSetting;
    
    // 판매자 삭제를 위한 양방향 매핑 추가
    @OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE)
    private List<Store> stores;
      
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
        
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public void setDoneClickCountSetting(boolean doneClickCountSetting) {
        this.doneClickCountSetting = doneClickCountSetting;
    }
    
    public static SellerDTO toDTO(Seller seller) {
        return SellerDTO.builder()
                .loginId(seller.getLoginId())
                .username(seller.getUsername())
                .password(seller.getPassword())
                .email(seller.getEmail())
                .mobile(seller.getMobile())
                .doneClickCountSetting(seller.getDoneClickCountSetting())
                .build();
    }    
}