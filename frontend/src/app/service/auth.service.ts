import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  sendOtp(email: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/send-otp?email=${email}`,
      {},
      { responseType: 'text' },
    );
  }

  verifyOtp(email: string, otp: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/verify-otp?email=${email}&otp=${otp}`,
      {},
      { responseType: 'text' },
    );
  }

  resendOtp(email: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/resend-otp?email=${email}`,
      {},
      { responseType: 'text' },
    );
  }

  register(user: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, user);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, data).pipe(
      tap((response: any) => {
        const token = response.token || response.jwt || response.accessToken;
        if (token) {
          this.saveToken(token);
          console.log('Token successfully saved to localStorage');
        }
      }),
    );
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/forgot-password?email=${email}`,
      {},
      { responseType: 'text' },
    );
  }

  resetPassword(
    email: string,
    otp: string,
    newPassword: string,
  ): Observable<any> {
    return this.http.post(
      `${this.baseUrl}/reset-password?email=${email}&otp=${otp}&newPassword=${newPassword}`,
      {},
      { responseType: 'text' },
    );
  }

  saveToken(token: string) {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/logout`, {}).pipe(
      tap(() => {
        localStorage.clear();
        sessionStorage.clear();
      }),
    );
  }
}
