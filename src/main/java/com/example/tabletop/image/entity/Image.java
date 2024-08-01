package com.example.tabletop.image.entity;

import com.example.tabletop.image.enums.ImageParentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자, 외부에서 직접 사용 방지
@Getter
@ToString
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Column(name = "parent_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ImageParentType parentType;

    // UUID + fileOriginalName
    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "file_original_name", nullable = false)
    private String fileOriginalName;
    
    @Column(name = "filepath", nullable = false)
    private String filepath;
    
    @Column(name = "s3_url", nullable = false)
    private String S3Url;
    
    @Builder
    public Image(Long parentId, ImageParentType parentType, String filename, String fileOriginalName, String filepath, String S3Url) {
        this.parentId = parentId;
        this.parentType = parentType;
        this.filename = filename;
        this.fileOriginalName = fileOriginalName;
        this.filepath = filepath;
        this.S3Url = S3Url;
    }

}
