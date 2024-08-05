package com.example.tabletop.sales.controller;

import com.example.tabletop.sales.dto.*;
import com.example.tabletop.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {
    private final SalesService salesService;
    
    @GetMapping("/{storeId}/daily-comparison")
    public ResponseEntity<DailyComparisonDTO> getDailyComparison(@PathVariable Long storeId) {
        return ResponseEntity.ok(salesService.getDailyComparison(storeId));
    }

    @GetMapping("/{storeId}/menu-sales")
    public ResponseEntity<List<MenuSalesDTO>> getMenuSales(@PathVariable Long storeId) {
        return ResponseEntity.ok(salesService.getMenuSales(storeId));
    }

    @GetMapping("/{storeId}/monthly")
    public ResponseEntity<List<MonthlySalesDTO>> getMonthlySales(@PathVariable Long storeId) {
        return ResponseEntity.ok(salesService.getMonthlySales(storeId));
    }

    @GetMapping("/{storeId}/weekly")
    public ResponseEntity<List<WeeklySalesDTO>> getWeeklySales(@PathVariable Long storeId) {
        return ResponseEntity.ok(salesService.getWeeklySales(storeId));
    }

    @GetMapping("/{storeId}/weekly-daily")
    public ResponseEntity<List<DailySalesDTO>> getWeeklyDailySales(@PathVariable Long storeId) {
        return ResponseEntity.ok(salesService.getWeeklyDailySales(storeId));
    }
}