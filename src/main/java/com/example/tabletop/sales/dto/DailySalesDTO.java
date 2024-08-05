package com.example.tabletop.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailySalesDTO {
    private String day;
    private int totalSales;
    private int orderCount;
}