package com.createorder.createorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.createorder.createorder.model.Order;
import com.createorder.createorder.service.OrderService;

import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("api/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    private final MeterRegistry registry;

    public OrderController(MeterRegistry registry) {
        this.registry = registry;
    }

    private final Logger LOG = LoggerFactory.getLogger(OrderController.class);
    
    @CrossOrigin
    @PostMapping("/create")
    public ResponseEntity<String> createNewOrder(@RequestParam String username, 
                                                @RequestParam String product,
                                                @RequestParam Integer amount, 
                                                @RequestParam Double price,
                                                @RequestParam String message_flag) 
    {
        Order order = orderService.CreateNewOrder(username, product, amount, price, message_flag);

        registry.counter("createdOrders.total").increment();

        // LabelMarker marker = LabelMarker.of("orderId", () -> String.valueOf(order.getId()));
        LOG.info("Order by " + username + " successfully created: " + order.getId());

        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @PostMapping("/fail")
    public ResponseEntity<String> failCreateOrder(@RequestParam String username, 
                                                @RequestParam String product,
                                                @RequestParam Integer amount, 
                                                @RequestParam Double price) 
    {
        Order order = orderService.failCreateOrder(username, product, amount, price);

        LOG.info("Order by " + username + " failed to be created " + order.getId());
        registry.counter("error.order.total").increment();

        return ResponseEntity.ok().build();
    }
}
