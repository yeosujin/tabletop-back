package com.example.tabletop.store.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.example.tabletop.store.enums.StoreType;

import lombok.Builder;
import lombok.Data;

@Data
public class StoreDetailsDTO {
	private Long storeId;
    private String name;
    private StoreType storeType;
    private String corporateRegistrationNumber;    
    private LocalDate openDate;
    private LocalDate closeDate;
    private String description;
    private String address;
    private String notice;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Set<String> holidays;
	private String sellerName;
	private String imageBase64;
    
    @Builder
    public StoreDetailsDTO(Long storeId, String name, StoreType storeType, String corporateRegistrationNumber,
    		LocalDate openDate, LocalDate closeDate, String description, String address, String notice,
    		LocalTime openTime, LocalTime closeTime, Set<String> holidays, String sellerName, String imageBase64) {

    	this.storeId = storeId;
    	this.name = name;
    	this.storeType = storeType;
    	this.corporateRegistrationNumber = corporateRegistrationNumber;
    	this.openDate = openDate;
    	this.closeDate = closeDate;
    	this.description = description;
    	this.address = address;
    	this.notice = notice;
    	this.openTime = openTime;
    	this.closeTime = closeTime;
    	this.holidays = holidays;
    	this.sellerName = sellerName;
    	this.imageBase64 = imageBase64;
    }
    
}
