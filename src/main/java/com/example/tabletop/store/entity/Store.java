package com.example.tabletop.store.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.tabletop.image.entity.Image;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.store.enums.Day;
import com.example.tabletop.store.enums.StoreType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Table(name = "store")
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    // nullable은 true이나 storetype에 따른 검증 로직 필요
    @Column(name = "corporate_registration_number", nullable = true, unique = true)
    private String corporateRegistrationNumber;
    
    // nullable은 true이나 storetype에 따른 검증 로직 필요
    @Column(name = "open_date", nullable = true)
    private LocalDate openDate;
    
    // nullable은 true이나 storetype에 따른 검증 로직 필요
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
    
    // 휴무일은 없거나 여러 개일 수 있음
    @ElementCollection(targetClass = Day.class)
    @CollectionTable(name = "holiday", joinColumns = @JoinColumn(name = "holiday_id"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "day", nullable = true)
    private Set<Day> holidays;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
	
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
	
    @ManyToOne
	@JoinColumn(name = "seller_id")
	@ToString.Exclude
	private Seller seller;
    
    @OneToOne(mappedBy = "store", cascade = CascadeType.REMOVE)
    private Image image;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Menu> menus;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
	private List<Order> orders;

    @Builder
	public Store(Long storeId, String name, StoreType storeType, String corporateRegistrationNumber, LocalDate openDate,
			LocalDate closeDate, String description, String address, String notice, LocalTime openTime,
			LocalTime closeTime, Set<Day> holidays, LocalDateTime createdAt, LocalDateTime updatedAt, Seller seller) {
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
		this.seller = seller;
	}
    
    // 가게 수정
    public void updateDetails(String name, String description, String address, String notice, LocalTime openTime,
			LocalTime closeTime, Set<Day> holidays) {
    	this.name = name;
		this.description = description;
		this.address = address;
		this.notice = notice;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.holidays = holidays;
    }
    
}

