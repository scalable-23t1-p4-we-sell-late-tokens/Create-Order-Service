package com.createorder.createorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.github.loki4j.slf4j.marker.LabelMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.createorder.createorder.model.Order;
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

    private final Logger LOG = LoggerFactory.getLogger(OrderController.class);
    
    @PostMapping("/create")
    public ResponseEntity<String> createNewOrder(@RequestParam String username, 
                                                @RequestParam String product,
                                                @RequestParam Integer amount, 
                                                @RequestParam Double price) 
    {
        Order order = orderService.CreateNewOrder(username, product, amount, price);

        registry.counter("createdOrders.total", "username", username).increment();

        // LabelMarker marker = LabelMarker.of("orderId", () -> String.valueOf(order.getId()));
        LOG.info("Order by " + username + " successfully created: " + order.getId());

        return ResponseEntity.ok().build();
    }
}
