package com.example.tabletop.image.repository;

import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.enums.ImageParentType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByParentIdAndParentType(Long parentId, ImageParentType parentType);

}
