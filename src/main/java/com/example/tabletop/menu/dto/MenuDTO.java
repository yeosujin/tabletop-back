package com.example.tabletop.menu.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MenuDTO {
    private String name;
    private Integer price;
    private String description;
    private Boolean isAvailable = true;
    private MultipartFile image;
}