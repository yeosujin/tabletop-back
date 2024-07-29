package com.example.tabletop.store.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.store.enums.StoreType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequestDTO {
	private String name;
    private String storeType;
    private String corporateRegistrationNumber;
    private String openDate;
    private String closeDate;
    private String description;
    private String address;
    private String notice;
    private String openTime;
    private String closeTime;
    private String[] holidays;

	@Builder
	public StoreRequestDTO(String name, String storeType, String corporateRegistrationNumber, String openDate,
			String closeDate, String description, String address, String notice, String openTime, String closeTime,
			String[] holidays) {
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
	}

}
