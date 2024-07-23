package com.example.tabletop.seller.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "login_id", length = 30)
    private String loginId;

    @Column(name = "username", length = 30)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "mobile", length = 14)
    private String mobile;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "done_click_count_setting")
    private Boolean doneClickCountSetting;
}
