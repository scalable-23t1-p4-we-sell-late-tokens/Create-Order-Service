package com.createorder.createorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.createorder.createorder.model.Message;
import com.createorder.createorder.model.Order;
import com.createorder.createorder.model.OrderStatus;
import com.createorder.createorder.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    Jedis jedis = new Jedis("redis", 6379);
    
    public Order CreateNewOrder(String username, String product, Integer amount, Double price, String message_flag) {
        Order newOrder = new Order(username, product, amount, price);
        newOrder.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(newOrder);

        publishToPayment(newOrder.getId(), username, product, amount, price, message_flag);

        return newOrder;
    }

    // send order progress to payment service
    public void publishToPayment(String id, String username, String product, Integer amount, Double price, String message_flag) {
        try {
            Message messageObject = new Message(id, username, product, amount, price, message_flag);
            String message = objectMapper.writeValueAsString(messageObject);
            jedis.publish("orderToPayment", message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    // Change the status of the order from the delivery service or payment rollback
    public void changeStatus(String id, OrderStatus status) {
        Order getOrder = orderRepository.findOrderById(id);
        getOrder.setStatus(status);
        orderRepository.save(getOrder);
    }
}
