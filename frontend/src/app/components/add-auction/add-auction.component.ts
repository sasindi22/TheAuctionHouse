import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuctionService } from '../../service/auction.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-add-auction',
  imports: [NavbarComponent, RouterModule, FormsModule, CommonModule],
  templateUrl: './add-auction.component.html',
  styleUrl: './add-auction.component.css',
})
export class AddAuctionComponent {
  auctionData = {
    title: '',
    description: '',
    category: '',
    minimumPrice: 0,
    endTime: '',
    shippingOption: 'Standard',
    image1: '',
    image2: '',
    image3: '',
  };

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

  constructor(
    private auctionService: AuctionService,
    private router: Router,
  ) {}

  onFileSelected(event: any, index: number) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64String = reader.result as string;
        if (index === 1) this.auctionData.image1 = base64String;
        if (index === 2) this.auctionData.image2 = base64String;
        if (index === 3) this.auctionData.image3 = base64String;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit() {
    if (
      !this.auctionData.image1 ||
      !this.auctionData.image2 ||
      !this.auctionData.image3
    ) {
      alert('Please upload all 3 images.');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      alert('You must be logged in to create an auction.');
      return;
    }

    this.auctionService.createAuction(this.auctionData, token).subscribe({
      next: (res) => {
        alert('Auction published successfully!');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error(err);
        alert('Failed to publish auction. Ensure you are logged in.');
      },
    });
  }
}
