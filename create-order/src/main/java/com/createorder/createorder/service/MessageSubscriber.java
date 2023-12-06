package com.createorder.createorder.service;

import org.springframework.stereotype.Service;

import com.createorder.createorder.controller.OrderController;
import com.createorder.createorder.model.Order;
import com.createorder.createorder.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Service
public class MessageSubscriber implements MessageListener {

    @Autowired
    OrderService orderService;

    private final ObjectMapper objectMapper;
    private final MeterRegistry registry;

    public MessageSubscriber(ObjectMapper objectMapper, MeterRegistry registry) {
        this.objectMapper = objectMapper;
        this.registry = registry;
    }

    private final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String receivedMessage = new String(message.getBody());

        if(channel.equals("delivery_status")) {
            fromDelivery(receivedMessage);
        }
        if (channel.equals("payment_rollback")) {
            paymentRollback(receivedMessage);
        }
    }

    // Receive message from delivery service
    private void fromDelivery(String message) {
        System.out.println("Message from delivery service: " + message);

        try {
            Order order = objectMapper.readValue(message, Order.class);
            String id = order.getId();
            OrderStatus status = order.getStatus();

            LOG.info("order " + id + " is successfully delivered");
            registry.counter("completed.orders.total").increment();
            
            orderService.changeStatus(id, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Recieve rollback message from payment service
    private void paymentRollback(String message) {
        System.out.println("Message from payment service: " + message);

        try {
            Order order = objectMapper.readValue(message, Order.class);
            String id = order.getId();
            OrderStatus status = order.getStatus();

            LOG.error("Rollback from payment service for order " + id + " with error status " + status);
            registry.counter("error.orders.total").increment();

            orderService.changeStatus(id, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
