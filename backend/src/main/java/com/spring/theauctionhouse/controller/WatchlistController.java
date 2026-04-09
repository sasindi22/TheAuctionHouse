package com.spring.theauctionhouse.controller;

import com.spring.theauctionhouse.entity.SavedBid;
import com.spring.theauctionhouse.service.SavedBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WatchlistController {
    private final SavedBidService savedBidService;

    @PostMapping("/toggle/{auctionId}")
    public ResponseEntity<String> toggle(@PathVariable Long auctionId, Principal principal) {
        String message = savedBidService.toggleSavedItem(auctionId, principal.getName());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/my-list")
    public ResponseEntity<List<SavedBid>> getMyList(Principal principal) {
        return ResponseEntity.ok(savedBidService.getMyWatchlist(principal.getName()));
    }
}
