package com.example.tabletop.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyComparisonDTO {
    private int yesterday;
    private int today;
}