package com.example.tabletop.orderitem.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // 판매자 삭제을 위한 remove 적용
    @JoinColumn(name = "order_id")
    private Order order;
}