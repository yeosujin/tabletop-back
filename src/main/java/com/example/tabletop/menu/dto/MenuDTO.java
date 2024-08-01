package com.example.tabletop.menu.dto;

import lombok.Data;

@Data
public class MenuDTO {
    private Long id;
    private Long storeId;
    private String name;
    private Integer price;
    private String description;
    private Boolean isAvailable = true;
}