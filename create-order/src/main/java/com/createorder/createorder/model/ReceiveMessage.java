package com.createorder.createorder.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveMessage {
    String order_id;
    String username;
    Long amount;
    String item_name;
    Double price;
    OrderStatus message_flag;
    OrderStatus message_response;
}
