package com.example.tabletop.store.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

<<<<<<< Updated upstream
import com.example.tabletop.seller.entity.Seller;
<<<<<<< Updated upstream
=======
import com.example.tabletop.store.enums.Day;
=======
>>>>>>> Stashed changes
>>>>>>> Stashed changes
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
=======
<<<<<<< Updated upstream
    private Set<Day> holidays;
	private Seller seller;
=======
    private Set<String> holidays;
	private String sellerName;
	private String imageFilePath;
	private String imageBase64;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    
    @Builder
    public StoreDetailsDTO(Long storeId, String name, StoreType storeType, String corporateRegistrationNumber,
    		LocalDate openDate, LocalDate closeDate, String description, String address, String notice,
<<<<<<< Updated upstream
    		LocalTime openTime, LocalTime closeTime, Set<String> holidays, String sellerName) {
=======
<<<<<<< Updated upstream
    		LocalTime openTime, LocalTime closeTime, Set<Day> holidays, Seller seller) {
>>>>>>> Stashed changes
    	super();
=======
    		LocalTime openTime, LocalTime closeTime, Set<String> holidays, String sellerName, String imageFilePath, String imageBase64) {
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
=======
<<<<<<< Updated upstream
    	this.seller = seller;
=======
    	this.sellerName = sellerName;
    	this.imageFilePath = imageFilePath;
    	this.imageBase64 = imageBase64;
>>>>>>> Stashed changes
>>>>>>> Stashed changes
    }
    
}
