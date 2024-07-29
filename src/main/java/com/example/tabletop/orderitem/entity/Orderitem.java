package com.example.tabletop.orderitem.entity;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Orderitem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderitem_id", nullable = false)
    private Long orderItemId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "menu_id", nullable = true)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}