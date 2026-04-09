package com.spring.theauctionhouse.repository;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.AuctionStatus;
import com.spring.theauctionhouse.entity.SavedBid;
import com.spring.theauctionhouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedBidRepository extends JpaRepository<SavedBid, Long> {

    Optional<SavedBid> findByUserAndAuctionItem(User user, AuctionItem item);
    List<SavedBid> findByUserEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM SavedBid s WHERE s.auctionItem.status != 'ONGOING'")
    void deleteExpiredSavedItems();
}
