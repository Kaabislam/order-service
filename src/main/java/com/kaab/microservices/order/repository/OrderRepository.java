package com.kaab.microservices.order.repository;
import com.kaab.microservices.order.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
