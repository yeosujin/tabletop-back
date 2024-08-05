package com.example.tabletop.sales.service;

import com.example.tabletop.sales.repository.SalesRepository;
import com.example.tabletop.sales.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final SalesRepository salesRepository;

    public DailyComparisonDTO getDailyComparison(Long storeId) {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime yesterday = today.minusDays(1);

        Integer todaySales = salesRepository.getSalesBetweenDates(today, LocalDateTime.now(), storeId);
        Integer yesterdaySales = salesRepository.getSalesBetweenDates(yesterday, today, storeId);

        return new DailyComparisonDTO(
            yesterdaySales != null ? yesterdaySales : 0, 
            todaySales != null ? todaySales : 0
        );
    }

    public List<MenuSalesDTO> getMenuSales(Long storeId) {
        return salesRepository.getMenuSales(storeId).stream()
                .map(result -> new MenuSalesDTO(
                    (String) result[0],
                    ((Number) result[1]).intValue(),
                    ((Number) result[2]).intValue()
                ))
                .collect(Collectors.toList());
    }

    public List<MonthlySalesDTO> getMonthlySales(Long storeId) {
        return salesRepository.getMonthlySales(storeId).stream()
                .map(result -> new MonthlySalesDTO(
                    (String) result[0],
                    ((Number) result[1]).intValue(),
                    ((Number) result[2]).intValue()
                ))
                .collect(Collectors.toList());
    }

    public List<WeeklySalesDTO> getWeeklySales(Long storeId) {
        return salesRepository.getWeeklySales(storeId).stream()
                .map(result -> new WeeklySalesDTO(
                    (String) result[0],
                    ((Number) result[1]).intValue(),
                    ((Number) result[2]).intValue()
                ))
                .collect(Collectors.toList());
    }

    public List<DailySalesDTO> getWeeklyDailySales(Long storeId) {
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay().minusDays(7);
        return salesRepository.getWeeklyDailySales(startOfWeek, storeId).stream()
                .map(result -> new DailySalesDTO(
                    (String) result[0],
                    ((Number) result[1]).intValue(),
                    ((Number) result[2]).intValue()
                ))
                .collect(Collectors.toList());
    }
}