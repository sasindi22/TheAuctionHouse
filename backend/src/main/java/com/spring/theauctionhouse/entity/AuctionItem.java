package com.spring.theauctionhouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuctionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String image1;

    @Column(columnDefinition = "LONGTEXT")
    private String image2;

    @Column(columnDefinition = "LONGTEXT")
    private String image3;

    private double minimumPrice;
    private double currentHighBid;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String category;
    private String shippingOption;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status = AuctionStatus.ONGOING;

    @Version
    private  Long version;

}
