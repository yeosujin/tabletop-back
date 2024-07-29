package com.example.tabletop.store.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tabletop.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>{
	
	// 특정 판매자의 가게 목록 조회
	List<Store> findAllBySeller_LoginId(String loginId);

	// 사업자등록번호가 DB에 이미 존재하는지 확인
    boolean existsByCorporateRegistrationNumber(String corporateRegistrationNumber);
    
//    void deleteBySellerId(Long sellerId); 

}
