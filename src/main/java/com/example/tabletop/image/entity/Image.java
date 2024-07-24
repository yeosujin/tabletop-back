package com.example.tabletop.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
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
    private Long id;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Column(name = "parent_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ParentType parentType;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "file_original_name", nullable = false)
    private String fileOriginalName;
    
    @Column(name = "filepath", nullable = false)
    private String filepath;

    public enum ParentType {
        STORE,
        MENU
    }

    // 모든 필드를 초기화하는 생성자
    public Image(Long parentId, ParentType parentType, String filename, String fileOriginalName, String filepath) {
        this.parentId = parentId;
        this.parentType = parentType;
        this.filename = filename;
        this.fileOriginalName = fileOriginalName;
        this.filepath = filepath;
    }
}