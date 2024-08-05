package com.example.tabletop.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuSalesDTO {
    private String menuName;
    private int quantitySold;
    private int totalSales;
}