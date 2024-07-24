package com.example.tabletop.payment.repository;

import com.example.tabletop.payment.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {}
