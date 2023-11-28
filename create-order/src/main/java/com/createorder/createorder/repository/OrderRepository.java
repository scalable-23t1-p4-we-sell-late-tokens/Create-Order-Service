package com.createorder.createorder.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.createorder.createorder.model.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, String> {
    
}
