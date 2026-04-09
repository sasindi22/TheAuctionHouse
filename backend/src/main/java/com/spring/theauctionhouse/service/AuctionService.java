package com.spring.theauctionhouse.service;

import com.spring.theauctionhouse.entity.*;
import com.spring.theauctionhouse.repository.AuctionRepository;
import com.spring.theauctionhouse.repository.BidRepository;
import com.spring.theauctionhouse.repository.OrderRepository;
import com.spring.theauctionhouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final OrderRepository orderRepository;

    public AuctionItem createAuction(AuctionItem item, String email) {
        User owner = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        item.setOwner(owner);
        item.setCurrentHighBid(item.getMinimumPrice());
        item.setStartTime(LocalDateTime.now());
        item.setStatus(AuctionStatus.ONGOING);
        return auctionRepository.save(item);
    }

    public AuctionItem updateAuction(Long id, AuctionItem details, String email) {
        AuctionItem item = getAuctionById(id);
        if (!item.getOwner().getEmail().equals(email)) throw new RuntimeException("Unauthorized");

        item.setTitle(details.getTitle());
        item.setDescription(details.getDescription());
        item.setImage1(details.getImage1());
        item.setImage2(details.getImage2());
        item.setImage3(details.getImage3());
        item.setCategory(details.getCategory());
        item.setShippingOption(details.getShippingOption());
        item.setEndTime(details.getEndTime());
        return auctionRepository.save(item);
    }

    public void deleteAuction(Long id, String email) {
        AuctionItem item = getAuctionById(id);
        if (!item.getOwner().getEmail().equals(email)) throw new RuntimeException("Unauthorized");
        auctionRepository.delete(item);
    }

    public List<AuctionItem> getMyOngoing(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return auctionRepository.findByOwnerIdAndStatus(user.getId(), AuctionStatus.ONGOING);
    }

    public List<AuctionItem> getMyHistory(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return auctionRepository.findMyHistory(user.getId());
    }

    public List<AuctionItem> globalSearch(String keyword, String category) {
        if (keyword != null && category != null) return auctionRepository.findByCategoryAndKeyword(category, keyword);
        if (keyword != null) return auctionRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        if (category != null) return auctionRepository.findByCategoryIgnoreCase(category);
        return auctionRepository.findAll();
    }

    public List<AuctionItem> getByPriceRange(Double min, Double max) { return auctionRepository.findByCurrentHighBidBetween(min, max); }

    public List<AuctionItem> getByShipping(String option) { return auctionRepository.findByShippingOptionIgnoreCase(option); }

    public List<AuctionItem> getAll() { return auctionRepository.findAll(); }

    public AuctionItem getAuctionById(Long id) {
        return auctionRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Transactional
    public void processEndedAuction(Long auctionId) {
        AuctionItem item = auctionRepository.findById(auctionId).orElseThrow();
        List<Bid> history = bidRepository.findByAuctionItemIdOrderByAmountDesc(auctionId);

        if (history.isEmpty()) {
            item.setStatus(AuctionStatus.EXPIRED);
        } else {
            item.setStatus(AuctionStatus.AWAITING_PAYMENT);
            item.setStartTime(LocalDateTime.now());
        }
        auctionRepository.save(item);
    }

    @Transactional
    public void fulfillOrder(Long auctionId) {
        AuctionItem item = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));
        item.setStatus(AuctionStatus.SOLD);
        auctionRepository.save(item);
        Order order = orderRepository.findByAuction(item).orElseThrow(() -> new RuntimeException("Order record not found for this auction"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        System.out.println("Payment synced: Auction " + auctionId + " is SOLD and Order is PAID.");
    }

    public void markAsSold(Long id) {
        AuctionItem item = auctionRepository.findById(id).orElseThrow();
        item.setStatus(AuctionStatus.SOLD);
        auctionRepository.save(item);
    }
}