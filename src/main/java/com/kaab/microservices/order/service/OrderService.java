package com.kaab.microservices.order.service;

import com.kaab.microservices.order.client.InventoryClient;
import com.kaab.microservices.order.dto.OrderRequest;
import com.kaab.microservices.order.model.Order;
import com.kaab.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest orderRequest){
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(),orderRequest.quantity()) ;
        if(isProductInStock){
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
        }
        else {
           throw  new RuntimeException("Order with skucode " +orderRequest.skuCode() +" mot available");

        }



    }
}
