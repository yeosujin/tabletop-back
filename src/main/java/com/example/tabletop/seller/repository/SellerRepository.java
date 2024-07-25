package com.example.tabletop.seller.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabletop.seller.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {

}
