package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.NotificationType;
import com.spring.theauctionhouse.entity.Order;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.repository.OrderRepository;
import com.spring.theauctionhouse.service.AuctionService;
import com.spring.theauctionhouse.service.EmailService;
import com.spring.theauctionhouse.service.NotificationService;
import com.spring.theauctionhouse.util.PaymentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    private final AuctionService auctionService;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @GetMapping("/hash/{auctionId}")
    public ResponseEntity<Map<String, String>> getPaymentDetails(@PathVariable Long auctionId) {
        AuctionItem item = auctionService.getAuctionById(auctionId);

        String orderId = "AUCTION_" + item.getId();
        String currency = "USD";
        double amount = item.getCurrentHighBid();

        String hash = PaymentUtil.generateHash(merchantId, orderId, amount, currency, merchantSecret);

        Map<String, String> response = new HashMap<>();
        response.put("merchant_id", merchantId);
        response.put("order_id", orderId);
        response.put("amount", String.valueOf(amount));
        response.put("currency", currency);
        response.put("hash", hash);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/notify")
    public ResponseEntity<String> handleNotification(@RequestParam Map<String, String> params) {
        String orderId = params.get("order_id");
        String statusCode = params.get("status_code");

        if ("2".equals(statusCode)) {
            Long auctionId = Long.parseLong(orderId.replace("AUCTION_", ""));
            auctionService.fulfillOrder(auctionId);
            AuctionItem item = auctionService.getAuctionById(auctionId);
            Order order = orderRepository.findByAuction(item).orElseThrow();
            User buyer = order.getBuyer();
            User seller = item.getOwner();

            notificationService.createNotification(buyer, "Payment Successful",
                    "You have successfully claimed '" + item.getTitle() + "'.", NotificationType.WON, true);

            notificationService.createNotification(seller, "Item Paid For!",
                    "The buyer has paid for '" + item.getTitle() + "'. Please ship the item.", NotificationType.SOLD, true);

            emailService.sendEmail(buyer.getEmail(), "Payment Confirmation",
                    "Hi " + buyer.getName() + ",\n\nYour payment of " + item.getCurrentHighBid() + " was successful.");

            emailService.sendEmail(seller.getEmail(), "Action Required: Item Paid",
                    "Hi " + seller.getName() + ",\n\nYour item '" + item.getTitle() + "' has been paid for. Please ship it soon.");

            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.ok("Failed");
    }
}