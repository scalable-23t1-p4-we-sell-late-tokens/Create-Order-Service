package com.createorder.createorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.createorder.createorder.model.Order;
import com.createorder.createorder.model.OrderStatus;
import com.createorder.createorder.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository createOrderRepository;
    
    public Order CreateNewOrder(String username, String product, Integer amount, Double price) {
        Order newOrder = new Order(username, product, amount, price * amount);
        newOrder.setStatus(OrderStatus.IN_PROGRESS);

        createOrderRepository.save(newOrder);
        return newOrder;
    }
}
