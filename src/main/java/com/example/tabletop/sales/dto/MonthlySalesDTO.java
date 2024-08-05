package com.example.tabletop.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlySalesDTO {
    private String month;
    private int totalSales;
    private int orderCount;
}