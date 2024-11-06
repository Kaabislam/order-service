package com.kaab.microservices.order.service;

import com.kaab.microservices.order.client.InventoryClient;
import com.kaab.microservices.order.dto.OrderRequest;
import com.kaab.microservices.order.event.OrderPlacedEvent;
import com.kaab.microservices.order.model.Order;
import com.kaab.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.kaab.microservices.order.client.InventoryClient.log;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest){
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(),orderRequest.quantity()) ;
        if(!isProductInStock){
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
            // send the message to kafka topic

            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(),orderRequest.userDetails().email());
            log.info("Start - sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End - sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);


        }
        else {
           throw  new RuntimeException("Order with skucode " +orderRequest.skuCode() +" mot available");

        }



    }
}
