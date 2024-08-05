package com.example.tabletop.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeeklySalesDTO {
    private String week;
    private int totalSales;
    private int orderCount;
}