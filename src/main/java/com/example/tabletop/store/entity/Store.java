package com.example.tabletop.store.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import com.example.tabletop.seller.entity.Seller;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@Entity
public class Store {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "store_id")
    private Long storeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "store_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private StoreType storeType;

    @Column(name = "corporate_registration_number", nullable = true)
    private String corporateRegistrationNumber;
    
    @Column(name = "open_date", nullable = true)
    private LocalDate openDate;
    
    @Column(name = "close_date", nullable = true)
    private LocalDate closeDate;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "notice", nullable = true)
    private String notice;
    
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;
	
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;
    
    @ElementCollection(targetClass = Day.class)
    @CollectionTable(name = "holiday", joinColumns = @JoinColumn(name = "holiday_id"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "day", nullable = true)
    private Set<Day> holidays;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
	
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
	
    @ManyToOne
	@JoinColumn(name = "seller_id")
	@ToString.Exclude
	private Seller seller;
    
	public enum StoreType {
        ORDINARY,
        TEMPORARY
    }
	
	public enum Day {
	    MONDAY,
	    TUESDAY,
	    WEDNESDAY,
	    THURSDAY,
	    FRIDAY,
	    SATURDAY,
	    SUNDAY
	}
}
