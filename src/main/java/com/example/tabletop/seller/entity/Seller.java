package com.example.tabletop.seller.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller")
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

}