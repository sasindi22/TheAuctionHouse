import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  email: string = '';
  isEmailSent: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  onSubmit() {
    if (!this.email) return;

    this.authService.forgotPassword(this.email).subscribe({
      next: (msg) => {
        alert(msg);
        this.isEmailSent = true;
        localStorage.setItem('resetEmail', this.email);
        this.router.navigate(['/reset-password']);
      },
      error: (err) => alert(err.error || 'Failed to send reset OTP'),
    });
  }
}
