import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  loginData = {
    email: '',
    password: '',
  };

  showPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit() {
    const savedEmail = localStorage.getItem('pendingEmail');
    if (savedEmail) {
      this.loginData.email = savedEmail;
      localStorage.removeItem('pendingEmail');
    }
  }

  onLogin() {
    this.authService.login(this.loginData).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.token);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        alert(err.error || 'Login failed. Please check your credentials.');
      },
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
}
