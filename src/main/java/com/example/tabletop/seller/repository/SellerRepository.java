package com.example.tabletop.seller.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabletop.seller.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {

	Optional<Seller> findByLoginId(String loginId);
	Optional<Seller> findByLoginIdAndUsernameAndMobile(String loginId, String username, String mobile);
}