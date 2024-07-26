package com.example.tabletop.seller.repository;

<<<<<<< Updated upstream
public interface SellerRepository {
=======
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.store.entity.Store;

public interface SellerRepository extends JpaRepository<Seller, Long> {
>>>>>>> Stashed changes

	Optional<Seller> findByLoginId(String loginId);

}
