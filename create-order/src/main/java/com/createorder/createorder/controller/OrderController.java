package com.createorder.createorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.createorder.createorder.service.OrderService;

import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    OrderService orderService;

    private final MeterRegistry registry;

    public OrderController(MeterRegistry registry) {
        this.registry = registry;
    }
    
    @PostMapping("/create")
    public ResponseEntity<String> createNewOrder(@RequestParam String username, 
                                                @RequestParam String product,
                                                @RequestParam Integer amount, 
                                                @RequestParam Double price) 
    {
        orderService.CreateNewOrder(username, product, amount, price);

        registry.counter("createdOrders.total", "username", username).increment();

        return ResponseEntity.ok().build();
    }
}
