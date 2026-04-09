import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  isMenuOpen = false;

  get isLoggedIn(): boolean {
    return !!this.authService.getToken();
  }

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
    document.body.style.overflow = 'auto';
  }

  onLogout() {
    this.authService.logout();
    this.isMenuOpen = false;
    this.router.navigate(['/login']);
    console.log('User logged out successfully');
  }
}
