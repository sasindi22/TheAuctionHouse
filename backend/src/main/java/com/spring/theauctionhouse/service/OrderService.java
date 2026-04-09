package com.spring.theauctionhouse.service;

import com.spring.theauctionhouse.entity.Order;
import com.spring.theauctionhouse.entity.OrderStatus;
import com.spring.theauctionhouse.repository.OrderRepository;
import com.spring.theauctionhouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order markAsShipped(Long orderId, String sellerEmail) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getSeller().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("You are not authorized to ship this item.");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Cannot ship an order that is not paid.");
        }
        order.setStatus(OrderStatus.SHIPPED);
        return orderRepository.save(order);
    }
}
