package com.example.tabletop.payment.entity;

import com.example.tabletop.order.entity.Order;
import com.example.tabletop.payment.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @OneToOne(cascade = CascadeType.REMOVE) // 판매자 삭제을 위한 remove 적용
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "is_refunded")
    private Boolean isRefunded;

    @Column(name = "transaction_id", length = 30)
    private String transactionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}