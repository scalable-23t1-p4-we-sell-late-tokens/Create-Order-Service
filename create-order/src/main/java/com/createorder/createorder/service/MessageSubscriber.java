package com.createorder.createorder.service;

import org.springframework.stereotype.Service;

import com.createorder.createorder.model.Order;
import com.createorder.createorder.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Service
public class MessageSubscriber implements MessageListener {

    @Autowired
    OrderService orderService;

    private final ObjectMapper objectMapper;

    public MessageSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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

            orderService.changeStatus(id, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void paymentRollback(String message) {
        System.out.println("Message from payment service: " + message);
    }
}
