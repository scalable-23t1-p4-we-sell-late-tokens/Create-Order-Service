package com.createorder.createorder.service;

import org.springframework.stereotype.Service;

import com.createorder.createorder.model.OrderStatus;
import com.createorder.createorder.model.ReceiveMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.instrumentation.annotations.WithSpan;

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

    private final Logger LOG = LoggerFactory.getLogger(MessageSubscriber.class);

    @WithSpan
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String receivedMessage = new String(message.getBody());

        if(channel.equals("deliveryToOrder")) {
            fromDelivery(receivedMessage);
        }
        if (channel.equals("paymentToOrder")) {
            paymentRollback(receivedMessage);
        }
    }

    // Receive message from delivery service
    private void fromDelivery(String message) {
        System.out.println("Message from delivery service: " + message);

        try {
            ReceiveMessage order = objectMapper.readValue(message, ReceiveMessage.class);
            String id = order.getOrder_id();
            OrderStatus status = order.getMessage_flag();
            
            System.out.println("message_flag: " + order.getMessage_flag());

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
            ReceiveMessage rollback = objectMapper.readValue(message, ReceiveMessage.class);
            String id = rollback.getOrder_id();
            OrderStatus response = null;
            if (rollback.getMessage_response().equals(OrderStatus.FORCED)) {
                response = OrderStatus.FAILED;
            } else {
                response = rollback.getMessage_response();
            }

            LOG.error("Rollback for order " + id + " with error status " + response);
            registry.counter("error.orders.total").increment();

            orderService.changeStatus(id, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
