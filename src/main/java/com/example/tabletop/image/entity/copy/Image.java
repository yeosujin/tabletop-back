package com.example.tabletop.image.entity.copy;

import java.time.LocalDateTime;

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
    private Long id;

    @Column(nullable = false)
    private Long parentId;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ParentType parentType;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String fileOriginalName;
    
    @Column(nullable = false)
    private String filepath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum ParentType {
        STORE,
        MENU
    }
}
