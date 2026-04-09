import { Component, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css',
})
export class VerifyEmailComponent {
  @ViewChildren('otpInput') inputs!: QueryList<ElementRef>;

  otpValues: string[] = ['', '', '', '', '', ''];

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  handleInput(event: any, index: number, element: HTMLInputElement) {
    const val = event.target.value;

    if (val) {
      this.otpValues[index] = val.slice(-1);
      element.value = this.otpValues[index];

      if (index < 5) {
        this.inputs.toArray()[index + 1].nativeElement.focus();
      }
    }
  }

  handleKeyDown(
    event: KeyboardEvent,
    index: number,
    element: HTMLInputElement,
  ) {
    if (event.key === 'Backspace') {
      if (!element.value && index > 0) {
        const prevInput = this.inputs.toArray()[index - 1].nativeElement;
        this.otpValues[index - 1] = '';
        prevInput.value = '';
        prevInput.focus();
      } else {
        this.otpValues[index] = '';
        element.value = '';
      }
    }
  }

  verifyCode() {
    const code = this.otpValues.join('');
    const userData = localStorage.getItem('pendingUser');

    if (!userData) {
      alert('Session expired. Please register again.');
      this.router.navigate(['/register']);
      return;
    }

    const user = JSON.parse(userData);

    this.authService.verifyOtp(user.email, code).subscribe({
      next: () => {
        this.authService
          .register({
            name: user.name,
            email: user.email,
            password: user.password,
          })
          .subscribe({
            next: () => {
              alert('Registration complete! Please login.');
              localStorage.removeItem('pendingUser');
              localStorage.setItem('pendingEmail', user.email);
              this.router.navigate(['/login']);
            },
            error: (err) =>
              alert('Registration failed: ' + (err.error || 'Server error')),
          });
      },
      error: (err) => {
        console.error(err);
        alert(err.error || 'OTP verification failed. Please check the code.');
      },
    });
  }

  resendOtp() {
    const userData = localStorage.getItem('pendingUser');
    if (!userData) {
      alert('Session expired. Please register again.');
      this.router.navigate(['/register']);
      return;
    }

    const email = JSON.parse(userData).email;

    this.authService.resendOtp(email).subscribe({
      next: () => alert('OTP resent successfully! Check your email.'),
      error: (err) => alert(err.error || 'Failed to resend OTP.'),
    });
  }
}
