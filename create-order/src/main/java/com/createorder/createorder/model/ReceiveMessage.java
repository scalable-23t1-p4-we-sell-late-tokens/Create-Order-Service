package com.createorder.createorder.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiveMessage {
    String order_id;
    OrderStatus message_flag;
    OrderStatus message_response;
}
