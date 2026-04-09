import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { WatchlistService } from '../../service/watchlist.service';

@Component({
  selector: 'app-saved-item',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  templateUrl: './saved-item.component.html',
  styleUrl: './saved-item.component.css',
})
export class SavedItemComponent implements OnInit {
  savedItems: any[] = [];

  constructor(private watchlistService: WatchlistService) {}

  ngOnInit() {
    this.loadSavedItems();
  }

  loadSavedItems() {
    this.watchlistService.getSavedAuctions().subscribe({
      next: (data: any[]) => {
        this.savedItems = data.map((save) => save.auctionItem);
      },
      error: (err) => console.error('Error loading saved items', err),
    });
  }

  onUnsave(auctionId: number) {
    this.watchlistService.toggleWatchlist(auctionId).subscribe({
      next: (response) => {
        this.savedItems = this.savedItems.filter(
          (item) => item.id !== auctionId,
        );
      },
      error: (err) => console.error('Could not unsave item', err),
    });
  }
}
