package com.example.tabletop.image.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
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

    // UUID 포함
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
}
