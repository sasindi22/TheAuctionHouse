package com.spring.theauctionhouse.repository;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.Order;
import com.spring.theauctionhouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(User buyer);
    List<Order> findBySeller(User seller);
    Optional<Order> findByAuction(AuctionItem auction);
}
