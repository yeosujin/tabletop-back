package com.example.tabletop.payment.repository;

import com.example.tabletop.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrder_OrderId(Long orderId);
    Optional<Payment> findByTransactionId(String transactionId);
}
