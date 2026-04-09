package com.spring.theauctionhouse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="saved_bid")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class SavedBid {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "auction_item_id")
    private AuctionItem auctionItem;

}