import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  getMyOrders(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my-orders`, {
      headers: this.getHeaders(),
    });
  }

  getMySales(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my-sales`, {
      headers: this.getHeaders(),
    });
  }

  markAsShipped(orderId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/${orderId}/ship`,
      {},
      { headers: this.getHeaders() },
    );
  }

  markAsCompleted(orderId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/${orderId}/complete`,
      {},
      { headers: this.getHeaders() },
    );
  }
}
