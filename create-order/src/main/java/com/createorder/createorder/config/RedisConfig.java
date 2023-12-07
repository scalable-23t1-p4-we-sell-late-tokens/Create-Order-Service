package com.createorder.createorder.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.createorder.createorder.service.MessageSubscriber;

@Configuration
public class RedisConfig {
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageSubscriber messageSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        List<ChannelTopic> channelTopics = Arrays.asList(
                new ChannelTopic("deliveryToOrder"),
                new ChannelTopic("paymentToOrder")
        );

        container.addMessageListener(messageSubscriber, channelTopics);
        return container;
    }
}
