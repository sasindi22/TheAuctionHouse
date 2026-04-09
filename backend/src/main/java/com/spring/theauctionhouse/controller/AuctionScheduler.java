package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.*;
import com.spring.theauctionhouse.repository.AuctionRepository;
import com.spring.theauctionhouse.repository.BidRepository;
import com.spring.theauctionhouse.repository.OrderRepository;
import com.spring.theauctionhouse.service.EmailService;
import com.spring.theauctionhouse.service.NotificationService;
import com.spring.theauctionhouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void checkClaimDeadlines() {

        LocalDateTime now = LocalDateTime.now();

        List<AuctionItem> finishedAuctions = auctionRepository.findFinishedOngoingAuctions(now);
        for (AuctionItem item : finishedAuctions) {
            processInitialEnding(item);
        }

        List<AuctionItem> awaiting = auctionRepository.findByStatus(AuctionStatus.AWAITING_PAYMENT);
        for (AuctionItem item : awaiting) {
            if (item.getStartTime().plusHours(24).isBefore(now)) {
                handleFirstBidderFailure(item);
            }
        }

        List<AuctionItem> backup = auctionRepository.findByStatus(AuctionStatus.BACKUP_PAYMENT);
        for (AuctionItem item : backup) {
            if (item.getStartTime().plusHours(24).isBefore(now)) {
                item.setStatus(AuctionStatus.UNCLAIMED);
                Optional<Order> optionalOrder = orderRepository.findByAuction(item);
                optionalOrder.ifPresent(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                });
                auctionRepository.save(item);
            }
        }
    }

    private void processInitialEnding(AuctionItem item) {
        List<Bid> bids = bidRepository.findByAuctionItemIdOrderByAmountDesc(item.getId());
        if (bids.isEmpty()) {
            item.setStatus(AuctionStatus.EXPIRED);
            notificationService.createNotification(item.getOwner(),
                    "Auction Expired", "Your item '" + item.getTitle() + "' expired without bids. " +
                            "Item will be deleted from the system if you dont relist. Relist item for another five days just with one click!",
                    NotificationType.EXPIRED, true);
        } else {
            item.setStatus(AuctionStatus.AWAITING_PAYMENT);
            item.setStartTime(LocalDateTime.now());
            User winner = bids.get(0).getUser();
            Order order = new Order();
            order.setAuction(item);
            order.setBuyer(winner);
            order.setSeller(item.getOwner());
            order.setAmount(bids.get(0).getAmount());
            order.setStatus(OrderStatus.PENDING_PAYMENT);
            order.setCreatedAt(LocalDateTime.now());
            orderRepository.save(order);

            notificationService.createNotification(winner,
                    "Auction Won!", "You won '" + item.getTitle() + "' for $" + item.getCurrentHighBid(),
                    NotificationType.WON, true);
            notificationService.createNotification(item.getOwner(),
                    "Item Sold!", "Your item '" + item.getTitle() + "' has a winner. Awaiting payment.",
                    NotificationType.SOLD, true);
            emailService.sendEmail(
                    winner.getEmail(),
                    "Congratulations! You won the auction: " + item.getTitle(),
                    "Hi " + winner.getName() + ",\n\n" +
                            "You have won the auction for '" + item.getTitle() + "' with a bid of $ " + item.getCurrentHighBid() + ".\n" +
                            "Please complete your payment within 24 hours to secure your item.\n\n" +
                            "Regards,\nThe Auction House Team"
            );
        }
        auctionRepository.save(item);
    }

    private void handleFirstBidderFailure(AuctionItem item) {
        List<Bid> bids = bidRepository.findByAuctionItemIdOrderByAmountDesc(item.getId());
        if (!bids.isEmpty()) {
            User flaker = bids.get(0).getUser();
            userService.applyStrike(flaker);

            if (bids.size() > 1) {
                User backupWinner = bids.get(1).getUser();
                item.setStatus(AuctionStatus.BACKUP_PAYMENT);
                item.setCurrentHighBid(bids.get(1).getAmount());
                item.setStartTime(LocalDateTime.now());
                notificationService.createNotification(backupWinner,
                        "New Opportunity!", "The previous winner failed to claim the item. " +
                                "'" + item.getTitle() + "' is now yours if you pay within next 24h.",
                        NotificationType.WON, true);

                Optional<Order> optionalOrder = orderRepository.findByAuction(item);
                if (optionalOrder.isPresent()) {
                    Order order = optionalOrder.get();
                    order.setBuyer(backupWinner);
                    order.setAmount(bids.get(1).getAmount());
                    order.setStatus(OrderStatus.PENDING_PAYMENT);
                    orderRepository.save(order);
                }

                emailService.sendEmail(
                        backupWinner.getEmail(),
                        "Opportunity: Auction item available for you! " + item.getTitle(),
                        "Hi " + backupWinner.getName() + ",\n\n" +
                                "The previous winner failed to complete the payment for '" + item.getTitle() + "'.\n" +
                                "As the next highest bidder, the item is now yours if you complete the payment of LKR " + bids.get(1).getAmount() + " within the next 24 hours.\n\n" +
                                "Regards,\nThe Auction House Team"
                );
            } else {
                item.setStatus(AuctionStatus.UNCLAIMED);
                notificationService.createNotification(item.getOwner(),
                        "Item Unclaimed", "The winner failed to pay for '" + item.getTitle() + "'. You can now relist it or it will get deleted after 24h.",
                        NotificationType.UNCLAIMED, true);
                Optional<Order> optionalOrder = orderRepository.findByAuction(item);
                optionalOrder.ifPresent(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                });
                User owner = item.getOwner();
                emailService.sendEmail(
                        owner.getEmail(),
                        "Auction Update: Your item '" + item.getTitle() + "' was not claimed",
                        "Hi " + owner.getName() + ",\n\n" +
                                "Unfortunately, the winner(s) did not complete the payment for your auction '" + item.getTitle() + "'.\n" +
                                "The auction has been marked as UNCLAIMED. You can now relist the item or check your dashboard for next steps.\n\n" +
                                "Regards,\nThe Auction House Team"
                );
            }
        }
        auctionRepository.save(item);
    }
}