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
<<<<<<< Updated upstream
    private Set<String> holidays;
	private String sellerName;
	private String imageBase64;
=======
<<<<<<< Updated upstream
    private Set<Day> holidays;
	private Seller seller;
=======
    private Set<String> holidays;
	private String sellerName;
	private String s3Url;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    
    @Builder
    public StoreDetailsDTO(Long storeId, String name, StoreType storeType, String corporateRegistrationNumber,
    		LocalDate openDate, LocalDate closeDate, String description, String address, String notice,
<<<<<<< Updated upstream
    		LocalTime openTime, LocalTime closeTime, Set<String> holidays, String sellerName, String imageBase64) {

=======
<<<<<<< Updated upstream
    		LocalTime openTime, LocalTime closeTime, Set<Day> holidays, Seller seller) {
    	super();
=======
    		LocalTime openTime, LocalTime closeTime, Set<String> holidays, String sellerName, String imageBase64, String s3Url) {
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    	this.sellerName = sellerName;
    	this.imageBase64 = imageBase64;
=======
<<<<<<< Updated upstream
    	this.seller = seller;
=======
    	this.sellerName = sellerName;
    	this.s3Url = s3Url;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    }
    
}
