import { Routes } from '@angular/router';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { VerifyEmailComponent } from './components/verify-email/verify-email.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AddAuctionComponent } from './components/add-auction/add-auction.component';
import { SingleAuctionComponent } from './components/single-auction/single-auction.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SavedItemComponent } from './components/saved-item/saved-item.component';
import { NotificationsComponent } from './components/notifications/notifications.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingPageComponent,
    title: 'The Auction House | Welcome'
  },
  { path: 'register', 
    component: RegisterComponent, 
    title: 'The Auction House | Register' 
  },
  { path: 'login', 
    component: LoginComponent, 
    title: 'The Auction House | Login' 
  },
  { path: 'verify-email', 
    component: VerifyEmailComponent, 
    title: 'The Auction House | Verify Email' 
  },
  { path: 'forgot-password', 
    component: ForgotPasswordComponent, 
    title: 'The Auction House | Forgot Password' 
  },
  { 
    path: 'reset-password', 
    component: ResetPasswordComponent,
    title: 'The Auction House | Reset Password' 
  },
  { path: 'dashboard', 
    component: DashboardComponent, 
    title: 'The Auction House | Dashboard' 
  },
  { path: 'auction/:id', 
    component: SingleAuctionComponent, 
    title: 'The Auction House | Single Auction Item' 
  },
  { path: 'add-auction', 
    component: AddAuctionComponent, 
    title: 'The Auction House | Add Auction' 
  },
  { path: 'profile', 
    component: ProfileComponent, 
    title: 'The Auction House | Profile' 
  },
  { path: 'saved-items', 
    component: SavedItemComponent, 
    title: 'The Auction House | Saved Auctions' 
  },
  { path: 'notifications', 
    component: NotificationsComponent, 
    title: 'The Auction House | Notifications' 
  },
  {
    path: '**',
    redirectTo: ''
  }
];