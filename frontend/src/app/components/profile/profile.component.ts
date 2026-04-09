import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../service/user.service';
import { AuctionService } from '../../service/auction.service';
import { BidService } from '../../service/bid.service';
import { NavbarComponent } from '../navbar/navbar.component';
import { Router, RouterModule } from '@angular/router';
import { OrderService } from '../../service/order.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, RouterModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  defaultImage = 'assets/user.png';
  user: any = { strikes: 0 };
  isEditing = false;

  activeTab: 'auctions' | 'bids' | 'won' = 'auctions';
  myAuctions: any[] = [];
  myBids: any[] = [];
  wonItems: any[] = [];

  constructor(
    private userService: UserService,
    private router: Router,
    private auctionService: AuctionService,
    private bidService: BidService,
    private orderService: OrderService,
    private http: HttpClient,
  ) {}

  ngOnInit() {
    this.loadProfile();
    this.loadData();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.user.profileImage = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  loadProfile() {
    const token = localStorage.getItem('token');
    if (token) {
      this.userService.getProfile(token).subscribe({
        next: (data) => {
          this.user = data;
        },
        error: (err) => {
          console.error('Profile Load Error:', err);
          if (err.status === 403 || err.status === 401) {
            alert('Session expired or unauthorized. Please login again.');
            this.router.navigate(['/login']);
          }
        },
      });
    } else {
      this.router.navigate(['/login']);
    }
  }

  onForgotPassword() {
    this.router.navigate(['/forgot-password']);
  }

  onSave() {
    const token = localStorage.getItem('token');
    if (token) {
      this.userService.updateProfile(this.user, token).subscribe({
        next: (updated) => {
          this.user = updated;
          this.isEditing = false;
          alert('Profile successfully updated.');
        },
        error: (err) => alert('Failed to update profile.'),
      });
    }
  }

  setActiveTab(tab: 'auctions' | 'bids' | 'won') {
    this.activeTab = tab;
    this.loadData();
  }

  loadData() {
    const token = localStorage.getItem('token');
    if (!token) return;
    this.myAuctions = [];
    this.myBids = [];
    this.wonItems = [];

    if (this.activeTab === 'auctions') {
      this.auctionService.getMyAuctions(token).subscribe((auctions) => {
        this.orderService.getMySales().subscribe((sales) => {
          const salesMap = new Map(sales.map((o) => [o.auction.id, o]));
          this.myAuctions = auctions.map((a) => ({
            ...a,
            orderStatus: salesMap.get(a.id)?.status,
          }));
        });
      });
    } else if (this.activeTab === 'bids') {
      this.bidService.getMyParticipation().subscribe((data) => {
        this.myBids = data.filter((a) => a.status === 'ONGOING');
      });
    } else if (this.activeTab === 'won') {
      this.orderService.getMyOrders().subscribe((data) => {
        this.wonItems = data;
      });
    }
  }

  onRelist(auctionId: number) {
    if (
      confirm(
        'Do you want to relist this item? It will be visible to bidders again.',
      )
    ) {
      this.auctionService.relistItem(auctionId).subscribe({
        next: () => {
          alert('Item has been successfully relisted!');
          this.loadData();
        },
        error: (err) => {
          console.error('Relist error:', err);
          alert(
            err.error?.message ||
              'Failed to relist the item. Please try again.',
          );
        },
      });
    }
  }

  shipItem(orderId: number) {
    this.orderService.markAsShipped(orderId).subscribe({
      next: () => {
        alert('Item marked as shipped!');
        this.loadData();
      },
      error: (err) => alert(err.error || 'Shipping update failed'),
    });
  }

  confirmDelivery(orderId: number) {
    this.orderService.markAsCompleted(orderId).subscribe({
      next: () => {
        alert('Order completed!');
        this.loadData();
      },
      error: (err) => alert('Confirmation failed'),
    });
  }

  onPay(order: any) {
    console.log('Initiating payment for Order ID:', order.id);
    const confirmPay = confirm(
      `Proceed to pay LKR ${order.amount} for ${order.auction?.title || 'this item'}?`,
    );
    if (confirmPay) {
      alert('Redirecting to Payment Gateway...');
    }
  }

  onClaim(order: any) {
  const payhere = (window as any).payhere;
  if (!payhere) return console.error('PayHere not loaded!');

  const auctionId = order.auction?.id;
  const token = localStorage.getItem('token');
  if (!token) return alert('Session expired. Please login again.');

  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

  this.http.get<any>(`http://localhost:8080/api/payment/hash/${auctionId}`, { headers })
    .subscribe({
      next: (payDetails) => {
        payhere.onCompleted = (orderId: string) => {
          alert('Payment successful!');
          this.loadData();
        };
        payhere.onDismissed = () => console.log('Payment dismissed');
        payhere.onError = (err: any) => console.error('PayHere error', err);

        const payment = {
          sandbox: true,
          merchant_id: payDetails.merchant_id,
          return_url: window.location.origin + '/profile',
          cancel_url: window.location.origin + '/profile',
          notify_url: 'http://localhost:8080/api/payment/notify',
          order_id: payDetails.order_id,
          items: order.auction.title,
          amount: payDetails.amount,
          currency: 'USD',
          hash: payDetails.hash,
          first_name: this.user.name,
          last_name: '',
          email: this.user.email,
          phone: this.user.mobile,
          address: this.user.address,
          city: 'Colombo',
          country: 'Sri Lanka',
        };

        payhere.startPayment(payment);
      },
      error: (err) => {
        console.error('Payment init failed', err);
        alert('Payment init failed: ' + (err.error?.message || 'Unauthorized'));
      }
    });
}

  get displayList() {
    if (this.activeTab === 'auctions') return this.myAuctions;
    if (this.activeTab === 'bids') return this.myBids;
    return this.wonItems;
  }

  testClick() {
    console.log('TEST CLICK WORKS');
  }
}
