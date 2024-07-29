package com.example.tabletop.store.enums;

public enum StoreType {
    ORDINARY("상시"),
    TEMPORARY("임시");  
	
	private String name;

	StoreType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
