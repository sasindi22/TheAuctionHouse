package com.spring.theauctionhouse.repository;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByAuctionItemIdOrderByAmountDesc(Long auctionItemId);

    @Query("SELECT DISTINCT b.auctionItem FROM Bid b WHERE b.user.email = :email")
    List<AuctionItem> findAuctionsParticipatedByUser(@Param("email") String email);

    long countByAuctionItemId(Long auctionItemId);
}
