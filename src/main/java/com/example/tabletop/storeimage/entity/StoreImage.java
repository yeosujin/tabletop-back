package com.example.tabletop.storeimage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
public class StoreImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_image_id")
    private Long storeImageId;

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
    public StoreImage(String filename, String fileOriginalName, String filepath, String S3Url) {
//        this.parentId = parentId;
//        this.parentType = parentType;
        this.filename = filename;
        this.fileOriginalName = fileOriginalName;
        this.filepath = filepath;
        this.S3Url = S3Url;
    }

}
