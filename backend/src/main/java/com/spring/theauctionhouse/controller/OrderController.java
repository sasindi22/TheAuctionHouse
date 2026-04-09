package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.Order;
import com.spring.theauctionhouse.entity.OrderStatus;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.repository.OrderRepository;
import com.spring.theauctionhouse.service.OrderService;
import com.spring.theauctionhouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserService userService;

    @GetMapping("/my-orders")
    public List<Order> getMyOrders(Principal principal) {
        User currentUser = userService.getProfile(principal.getName());
        return orderRepository.findByBuyer(currentUser);
    }

    @GetMapping("/my-sales")
    public List<Order> getMySales(Principal principal) {
        User currentUser = userService.getProfile(principal.getName());
        return orderRepository.findBySeller(currentUser);
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<Order> markAsShipped(@PathVariable Long orderId, Principal principal) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getSeller().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized: Only the seller can mark this as shipped.");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Order must be PAID before it can be shipped.");
        }
        order.setStatus(OrderStatus.SHIPPED);
        Order updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<Order> markAsCompleted(@PathVariable Long orderId, Principal principal) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getBuyer().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized: Only the buyer can confirm delivery.");
        }
        order.setStatus(OrderStatus.COMPLETED);
        return ResponseEntity.ok(orderRepository.save(order));
    }
}
