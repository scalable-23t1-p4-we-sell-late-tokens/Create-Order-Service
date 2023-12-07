package com.createorder.createorder.model;

public record Message(String order_id, String username, String item_name, Integer amount, Double price, String message_flag) {
    
} 
