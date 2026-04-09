import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { AuctionService } from '../../service/auction.service';
import { WatchlistService } from '../../service/watchlist.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  auctionItems: any[] = [];
  searchKeyword: string = '';
  showFilters = false;

  minPrice: number | null = null;
  maxPrice: number | null = null;

  categories: string[] = [
    'Fine Art',
    'Jewelry',
    'Furniture',
    'Antiques',
    'Home Decor',
    'Tableware',
    'Collectibles',
    'Rare Books',
    'Fashion',
  ];
  selectedCategory: string = 'All';
  selectedSort: string = 'popular';

  savedItemIds: Set<number> = new Set();

  constructor(
    private auctionService: AuctionService,
    private router: Router,
    private watchlistService: WatchlistService,
  ) {}

  ngOnInit() {
    this.loadAllItems();
    this.loadWatchlist();
  }

  loadAllItems() {
    this.auctionService.getAll().subscribe({
      next: (data) =>
        (this.auctionItems = data.map((item) => this.mapBackendItem(item))),
      error: (err) => console.error(err),
    });
  }

  private mapBackendItem(item: any) {
    return {
      ...item,
      imageUrl: item.image1,
      currentBid: item.currentHighBid,
      category: item.category,
      timeLeft: this.calculateTimeLeft(item.endTime),
    };
  }

  private calculateTimeLeft(endTime: string): string {
    const now = new Date();
    const end = new Date(endTime);
    const diff = end.getTime() - now.getTime();

    if (diff <= 0) return 'Ended';

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
    const minutes = Math.floor((diff / (1000 * 60)) % 60);

    return `${days}d ${hours}h ${minutes}m`;
  }

  onSearch() {
    this.auctionService
      .search(this.searchKeyword, this.selectedCategory)
      .subscribe({
        next: (data) =>
          (this.auctionItems = data.map((item) => this.mapBackendItem(item))),
        error: (err) => console.error(err),
      });
  }

  selectCategory(cat: string) {
    this.selectedCategory = cat;
    this.onSearch();
  }

  applyPriceFilter() {
    if (this.minPrice && this.maxPrice && this.minPrice > this.maxPrice) {
      [this.minPrice, this.maxPrice] = [this.maxPrice, this.minPrice];
    }

    if (this.minPrice != null && this.maxPrice != null) {
      this.auctionService
        .filterByPrice(this.minPrice, this.maxPrice)
        .subscribe({
          next: (data) =>
            (this.auctionItems = data.map((item) => this.mapBackendItem(item))),
          error: (err) => console.error(err),
        });
    }
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }

  applySort() {
    if (this.selectedSort === 'lowest') {
      this.auctionItems.sort((a, b) => a.currentBid - b.currentBid);
    } else if (this.selectedSort === 'highest') {
      this.auctionItems.sort((a, b) => b.currentBid - a.currentBid);
    } else {
      this.auctionItems.sort((a, b) => b.bids - a.bids);
    }
  }

  goToAuction(id: number) {
    this.router.navigate(['/auction', id]);
  }

  loadWatchlist() {
    const token = localStorage.getItem('token');
    if (!token) return;

    this.watchlistService.getSavedAuctions().subscribe({
      next: (savedItems) => {
        this.savedItemIds = new Set(
          savedItems.map((item: any) => item.auctionItem.id),
        );
      },
      error: (err) => console.error('Error loading watchlist', err),
    });
  }

  onToggleWatchlist(event: Event, auctionId: number) {
    event.stopPropagation();
    const token = localStorage.getItem('token');

    if (!token) {
      alert('Please login to save auctions.');
      return;
    }

    this.watchlistService.toggleWatchlist(auctionId).subscribe({
      next: () => {
        if (this.savedItemIds.has(auctionId)) {
          this.savedItemIds.delete(auctionId);
        } else {
          this.savedItemIds.add(auctionId);
        }
      },
      error: (err) => console.error('Toggle failed', err),
    });
  }

  isSaved(id: number): boolean {
    return this.savedItemIds.has(id);
  }
}
