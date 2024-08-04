package com.example.tabletop.menu.dto;

import com.example.tabletop.menu.entity.Menu;

import lombok.Data;

@Data
public class MenuDTO {
    private Long id;
    private Long storeId;
    private String name;
    private Integer price;
    private String description;
    private Boolean isAvailable = true;
    private String s3MenuUrl;
    
    public static MenuDTO fromEntity(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setName(menu.getName());
        dto.setPrice(menu.getPrice());
        dto.setDescription(menu.getDescription());
        dto.setIsAvailable(menu.getIsAvailable());
        dto.setS3MenuUrl(menu.getMenuimageUrl());
        return dto;
    }
}