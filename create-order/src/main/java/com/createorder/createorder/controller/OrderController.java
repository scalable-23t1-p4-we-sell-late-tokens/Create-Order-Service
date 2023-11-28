package com.createorder.createorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.createorder.createorder.service.OrderService;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    OrderService orderService;
    
    @PostMapping("/create")
    public ResponseEntity<String> createNewOrder(@RequestParam String username, 
                                                @RequestParam String product,
                                                @RequestParam Integer amount, 
                                                @RequestParam Double price) 
    {
        orderService.CreateNewOrder(username, product, amount, price);
        return ResponseEntity.ok().build();
    }
}
