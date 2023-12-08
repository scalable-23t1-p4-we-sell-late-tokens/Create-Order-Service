package com.createorder.createorder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.createorder.createorder.model.Message;
import com.createorder.createorder.model.Order;
import com.createorder.createorder.model.OrderStatus;
import com.createorder.createorder.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.instrumentation.annotations.WithSpan;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Tracer tracer;

    Jedis jedis = new Jedis("redis", 6379);
    
    public Order CreateNewOrder(String username, String product, Integer amount, Double price, String message_flag) {
        Order newOrder = new Order(username, product, amount, price);
        newOrder.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(newOrder);

        publishToPayment(newOrder.getId(), username, product, amount, price, message_flag);

        return newOrder;
    }

    public Order failCreateOrder(String username, String product, Integer amount, Double price) {
        Order newOrder = new Order(username, product, amount, price);
        newOrder.setStatus(OrderStatus.FAILED);
        orderRepository.save(newOrder);

        return newOrder;
    }

    // send order progress to payment service
    @WithSpan
    public void publishToPayment(String id, String username, String product, Integer amount, Double price, String message_flag) {
        Span span = tracer.spanBuilder("Publish Message").startSpan();
        try {
            Message messageObject = new Message(id, username, product, amount, price, message_flag);
            String message = objectMapper.writeValueAsString(messageObject);
            jedis.publish("orderToPayment", message);
            span.addEvent("Message published to payment-service");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
            span.end();
        }
    }

    // Change the status of the order from the delivery service or payment rollback
    @WithSpan
    public void changeStatus(String id, OrderStatus status) {
        Order getOrder = orderRepository.findOrderById(id);
        getOrder.setStatus(status);
        orderRepository.save(getOrder);
    }
}
