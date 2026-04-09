package com.spring.theauctionhouse.service;

import com.spring.theauctionhouse.entity.*;
import com.spring.theauctionhouse.repository.AuctionRepository;
import com.spring.theauctionhouse.repository.BidRepository;
import com.spring.theauctionhouse.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public Bid placeBid(Long auctionId, Double amount, String userEmail) {
        AuctionItem item = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));
        User bidder = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        if(item.getStatus() != AuctionStatus.ONGOING){
            throw new RuntimeException("Auction is no longer accepting bids");
        }

        if (item.getEndTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Auction has already ended");
        }

        if(item.getOwner().getEmail().equals(bidder.getEmail())){
            throw new RuntimeException("User cannot bid on their own auctions");
        }

        double currentPrice = item.getCurrentHighBid();
        double minRequiredIncrement = currentPrice * 0.05;
        double minNextBid = currentPrice + minRequiredIncrement;

        if (amount < minNextBid) {
            throw new RuntimeException(String.format("Bid is too low. To outbid, you must bid at least 5%% more: Minimum ", minNextBid));
        }

        item.setCurrentHighBid(amount);
        auctionRepository.save(item);

        List<Bid> history = bidRepository.findByAuctionItemIdOrderByAmountDesc(auctionId);
        if (!history.isEmpty()) {
            User previousBidder = history.get(0).getUser();
            if (!previousBidder.getEmail().equals(userEmail)) {
                notificationService.createNotification(previousBidder,
                        "You've been outbid!",
                        "Someone placed a higher bid on '" + item.getTitle() + ". Current bid: $" + amount + "'. Place new bid to win the auction!",
                        NotificationType.OUTBID, true);
            }
        }

        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setBidTime(LocalDateTime.now());
        bid.setUser(bidder);
        bid.setAuctionItem(item);

        return bidRepository.save(bid);
    }

    public List<Bid> getBidHistory(Long auctionId) {
        return bidRepository.findByAuctionItemIdOrderByAmountDesc(auctionId);
    }

    public List<AuctionItem> getMyBidParticipation(String email) {
        return bidRepository.findAuctionsParticipatedByUser(email);
    }

    public long getBidCount(Long auctionId) {
        return bidRepository.countByAuctionItemId(auctionId);
    }

}
