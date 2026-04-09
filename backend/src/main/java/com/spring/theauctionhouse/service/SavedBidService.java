package com.spring.theauctionhouse.service;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.AuctionStatus;
import com.spring.theauctionhouse.entity.SavedBid;
import com.spring.theauctionhouse.entity.User;
import com.spring.theauctionhouse.repository.AuctionRepository;
import com.spring.theauctionhouse.repository.SavedBidRepository;
import com.spring.theauctionhouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedBidService {
    private final SavedBidRepository savedBidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public String toggleSavedItem(Long auctionId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        AuctionItem item = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction item not found"));

        return savedBidRepository.findByUserAndAuctionItem(user, item)
                .map(saved -> {
                    savedBidRepository.delete(saved);
                    return "Removed from watchlist";
                })
                .orElseGet(() -> {
                    SavedBid newSave = new SavedBid();
                    newSave.setUser(user);
                    newSave.setAuctionItem(item);
                    savedBidRepository.save(newSave);
                    return "Added to watchlist";
                });
    }

    public List<SavedBid> getMyWatchlist(String email) {
        return savedBidRepository.findByUserEmail(email);
    }

    @Transactional
    public void cleanupExpired() {
        savedBidRepository.deleteExpiredSavedItems();
    }
}
