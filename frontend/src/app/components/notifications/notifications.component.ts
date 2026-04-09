import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { NavbarComponent } from "../navbar/navbar.component";
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../service/notification.service';
import { AuctionService } from '../../service/auction.service';

// interface Notification {
//   id: number;
//   type: 'OUTBID' | 'WON' | 'ENDING_SOON' | 'NEW_BID' | 'SYSTEM';
//   title: string;
//   message: string;
//   timestamp: Date;
//   read: boolean;
// }

@Component({
  selector: 'app-notifications',
  imports: [NavbarComponent, DatePipe, CommonModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.css'
})
export class NotificationsComponent implements OnInit {
  notifications: any[] = [];
  filteredNotifications: any[] = [];
  activeFilter: string = 'ALL';

  constructor(
    private notificationService: NotificationService,
    private auctionService: AuctionService
  ) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.notificationService.getNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.applyFilter();
      },
      error: (err) => console.error('Failed to load notifications', err)
    });
  }

  setActiveFilter(filter: string): void {
    this.activeFilter = filter;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.activeFilter === 'ALL') {
      this.filteredNotifications = this.notifications;
    } else if (this.activeFilter === 'WINS') {
      this.filteredNotifications = this.notifications.filter(n => n.type === 'WON');
    } else if (this.activeFilter === 'ITEMS') {
      this.filteredNotifications = this.notifications.filter(n => 
        ['SOLD', 'EXPIRED', 'UNCLAIMED', 'OUTBID'].includes(n.type)
      );
    }
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe(() => {
      this.notifications.forEach(n => n.read = true);
    });
  }

  dismissNotification(id: number): void {
    this.notificationService.deleteNotification(id).subscribe(() => {
      this.notifications = this.notifications.filter(n => n.id !== id);
      this.applyFilter();
    });
  }

  // Handle the "Relist" action from the Unclaimed/Expired notifications
  relist(auctionId: number, notificationId: number): void {
    this.auctionService.relistItem(auctionId).subscribe({
      next: () => {
        alert('Item relisted successfully for another 5 days!');
        this.dismissNotification(notificationId);
      },
      error: (err) => alert('Failed to relist item.')
    });
  }

  getNotificationIcon(type: string): string {
    const icons: any = {
      'OUTBID': 'fas fa-exclamation-triangle',
      'WON': 'fas fa-trophy',
      'EXPIRED': 'fas fa-hourglass-end',
      'SOLD': 'fas fa-check-circle',
      'UNCLAIMED': 'fas fa-hand-holding-usd',
      'STRIKE': 'fas fa-gavel'
    };
    return icons[type] || 'fas fa-bell';
  }
}