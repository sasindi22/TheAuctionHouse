import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent {
  otp: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  email: string | null = localStorage.getItem('resetEmail');

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  onSubmit() {
    if (!this.email) {
      alert('Session expired. Start again.');
      this.router.navigate(['/forgot-password']);
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      alert('Passwords do not match!');
      return;
    }

    this.authService
      .resetPassword(this.email, this.otp, this.newPassword)
      .subscribe({
        next: (msg) => {
          alert(msg);
          localStorage.removeItem('resetEmail');
          this.router.navigate(['/login']);
        },
        error: (err) => alert(err.error || 'Failed to reset password'),
      });
  }

  showPassword = false;
  showConfirmPassword = false;

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
}
