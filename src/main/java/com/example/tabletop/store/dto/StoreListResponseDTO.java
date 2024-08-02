package com.example.tabletop.store.dto;

import com.example.tabletop.store.enums.StoreType;

import lombok.Builder;
import lombok.Data;

@Data
public class StoreListResponseDTO {
	private Long storeId;
    private String name;
    private StoreType storeType;
    private String s3Url;

    @Builder
	public StoreListResponseDTO(Long storeId, String name, StoreType storeType, String s3Url) {
		super();
		this.storeId = storeId;
		this.name = name;
		this.storeType = storeType;
		this.s3Url = s3Url;
	}

}
