package com.createorder.createorder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    private String product;
    private Integer amount;
    private Double price;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order() { }

    public Order(String username, String product, Integer amount, Double price) {
        this.username = username;
        this.product = product;
        this.amount = amount;
        this.price = price;
    }

    public Order(String username, String product) {
        this.username = username;
        this.product = product;
    }
}
