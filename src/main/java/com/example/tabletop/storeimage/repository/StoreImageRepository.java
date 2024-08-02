package com.example.tabletop.storeimage.repository;

import com.example.tabletop.storeimage.entity.StoreImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {

//    List<StoreImage> findByParentIdAndParentType(Long parentId);

}
