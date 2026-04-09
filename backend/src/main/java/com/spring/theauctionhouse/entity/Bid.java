package com.spring.theauctionhouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Bid {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private LocalDateTime bidTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "auction_item_id")
    private AuctionItem auctionItem;

}
