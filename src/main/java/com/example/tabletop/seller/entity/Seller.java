package com.example.tabletop.seller.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Entity
public class Seller {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seller_id")
	private Long sellerId;

	@Column(name = "login_id", nullable = false, unique = true, length = 30)
	private String loginId;

	@Column(name = "username", nullable = false, length = 30)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "email", nullable = false, length = 50)
	private String email;

	@Column(name = "mobile", nullable = false, length = 14)
	private String mobile;

	@Column(name = "created_at", nullable = false)
	private LocalDate createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDate updatedAt;

	@Column(name = "refresh_token")
	private String refreshToken;

	@Column(name = "done_click_count_setting")
	private Boolean doneClickCountSetting;
}
