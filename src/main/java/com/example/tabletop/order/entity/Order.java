package com.example.tabletop.order.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.tabletop.orderitem.entity.Orderitem;
import com.example.tabletop.payment.entity.Payment;
import com.example.tabletop.store.entity.Store;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString(exclude = "store")
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "table_number")
    private Integer tableNumber;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "waiting_number")
    private Integer waitingNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // 판매자 삭제을 위한 remove 적용
    @JoinColumn(name = "store_id")
    private Store store;
    
    // 판매자 삭제를 위한 양방향 매핑 추가
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<Orderitem> orderItems;
    
    // 판매자 삭제를 위한 양방향 매핑 추가
    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE)
    private Payment payment;
}