package com.spring.theauctionhouse.repository;

import com.spring.theauctionhouse.entity.AuctionItem;
import com.spring.theauctionhouse.entity.AuctionStatus;
import com.spring.theauctionhouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionItem, Long> {

    List<AuctionItem> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    List<AuctionItem> findByOwner(User owner);
    List<AuctionItem> findByCategoryIgnoreCase(String category);

    @Query("SELECT a FROM AuctionItem a WHERE LOWER(a.category) = LOWER(:category) AND " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<AuctionItem> findByCategoryAndKeyword(@Param("category") String category, @Param("keyword") String keyword);

    List<AuctionItem> findByOwnerIdAndStatus(Long userId, AuctionStatus status);
    List<AuctionItem> findByStatus( AuctionStatus status);

    @Query("SELECT a FROM AuctionItem a WHERE a.owner.id = :userId AND a.status != 'ONGOING'")
    List<AuctionItem> findMyHistory(@Param("userId") Long userId);

    @Query("SELECT a FROM AuctionItem a WHERE a.status = 'ONGOING' AND a.endTime <= :now")
    List<AuctionItem> findFinishedOngoingAuctions(@Param("now") LocalDateTime now);

    List<AuctionItem> findByShippingOptionIgnoreCase(String shippingOption);
    List<AuctionItem> findByCurrentHighBidBetween(Double minPrice, Double maxPrice);
}