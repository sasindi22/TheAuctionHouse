import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { AuctionService } from '../../service/auction.service';
import { BidService } from '../../service/bid.service';
import { WatchlistService } from '../../service/watchlist.service';

@Component({
  selector: 'app-single-auction',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule, RouterModule],
  templateUrl: './single-auction.component.html',
  styleUrl: './single-auction.component.css',
})
export class SingleAuctionComponent implements OnInit, OnDestroy {
  item: any;
  bidAmount: number | null = null;
  bidHistory: any[] = [];
  bidCount: number = 0;
  mainImage: string = '';
  timeLeft: string = '';
  private timerInterval: any;

  savedItemIds: Set<number> = new Set();

  constructor(
    private route: ActivatedRoute,
    private auctionService: AuctionService,
    private bidService: BidService,
    private watchlistService: WatchlistService,
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadAuctionDetails(id);
      this.loadBidData(+id);
      this.loadWatchlistStatus();
    }
  }

  loadAuctionDetails(id: string) {
    this.auctionService.getById(id).subscribe({
      next: (data) => {
        this.item = data;
        this.mainImage = data.image1;
        this.startCountdown(data.endTime);
      },
      error: (err) => console.error('Error fetching details', err),
    });
  }

  loadBidData(id: number) {
    this.bidService.getBidHistory(id).subscribe({
      next: (history) => (this.bidHistory = history),
      error: (err) => console.error('Error fetching history', err),
    });

    this.bidService.getBidCount(id).subscribe({
      next: (count) => (this.bidCount = count),
      error: (err) => console.error('Error fetching count', err),
    });
  }

  get sortedBids() {
    return [...this.bidHistory].sort((a, b) => b.amount - a.amount);
  }

  onPlaceBid() {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Please login to place a bid.');
      return;
    }
    if (!this.bidAmount || this.bidAmount <= (this.item?.currentHighBid || 0)) {
      alert('Bid amount must be higher than the current highest bid.');
      return;
    }

    this.bidService.placeBid(this.item.id, this.bidAmount, token).subscribe({
      next: () => {
        alert('Bid placed successfully!');
        this.bidAmount = null;
        this.loadAuctionDetails(this.item.id);
        this.loadBidData(this.item.id);
      },
      error: (err) => alert(err.error?.message || 'Failed to place bid'),
    });
  }

  startCountdown(endTimeStr: string) {
    const endTime = new Date(endTimeStr).getTime();
    if (this.timerInterval) clearInterval(this.timerInterval);

    this.timerInterval = setInterval(() => {
      const now = new Date().getTime();
      const distance = endTime - now;

      if (distance < 0) {
        this.timeLeft = 'AUCTION ENDED';
        clearInterval(this.timerInterval);
        return;
      }

      const days = Math.floor(distance / (1000 * 60 * 60 * 24));
      const hours = Math.floor(
        (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60),
      );
      const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((distance % (1000 * 60)) / 1000);

      this.timeLeft = `${days}d ${hours}h ${minutes}m ${seconds}s`;
    }, 1000);
  }

  loadWatchlistStatus() {
    this.watchlistService.getSavedAuctions().subscribe({
      next: (items) => {
        this.savedItemIds = new Set(items.map((i) => i.auctionItem.id));
      },
      error: (err) => console.error('Error loading watchlist', err),
    });
  }

  isSaved(id: number): boolean {
    return this.savedItemIds.has(id);
  }

  onToggleWatchlist(event: Event, id: number) {
    event.preventDefault();
    event.stopPropagation();
    const token = localStorage.getItem('token');

    if (!token) {
      alert('Please login to save auctions.');
      return;
    }

    this.watchlistService.toggleWatchlist(id).subscribe({
      next: () => {
        if (this.isSaved(id)) {
          this.savedItemIds.delete(id);
        } else {
          this.savedItemIds.add(id);
        }
      },
      error: (err) => console.error('Toggle failed', err),
    });
  }

  setMainImage(imgUrl: string) {
    if (imgUrl) this.mainImage = imgUrl;
  }

  ngOnDestroy() {
    if (this.timerInterval) clearInterval(this.timerInterval);
  }
}
