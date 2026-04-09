package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.Bid;
import com.spring.theauctionhouse.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BidController {

    private final BidService bidService;

    @PostMapping("/place/{auctionId}")
    public ResponseEntity<?> placeBid(@PathVariable Long auctionId, @RequestParam Double amount, Principal principal) {
        try {
            return ResponseEntity.ok(bidService.placeBid(auctionId, amount, principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history/{auctionId}")
    public ResponseEntity<List<Bid>> getHistory(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getBidHistory(auctionId));
    }

    @GetMapping("/my-participation")
    public ResponseEntity<List<AuctionItem>> getMyParticipation(Principal principal) {
        return ResponseEntity.ok(bidService.getMyBidParticipation(principal.getName()));
    }

    @GetMapping("/count/{auctionId}")
    public ResponseEntity<Long> getBidCount(@PathVariable Long auctionId) {
        return ResponseEntity.ok(bidService.getBidCount(auctionId));
    }

}
