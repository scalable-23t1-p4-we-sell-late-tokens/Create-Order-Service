package com.createorder.createorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.createorder.createorder.model.Order;
import com.createorder.createorder.model.OrderStatus;
import com.createorder.createorder.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    public Order CreateNewOrder(String username, String product, Integer amount, Double price) {
        Order newOrder = new Order(username, product, amount, price * amount);
        newOrder.setStatus(OrderStatus.IN_PROGRESS);

        orderRepository.save(newOrder);
        return newOrder;
    }

    // Change the status of the order from the delivery service
    public void changeStatus(String id, OrderStatus status) {
        Order getOrder = orderRepository.findOrderById(id);
        getOrder.setStatus(status);
        orderRepository.save(getOrder);
    }
}
