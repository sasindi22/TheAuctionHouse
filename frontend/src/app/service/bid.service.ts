import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BidService {
  private apiUrl = 'http://localhost:8080/api/bids';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  placeBid(auctionId: number, amount: number, token: string): Observable<any> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(
      `${this.apiUrl}/place/${auctionId}?amount=${amount}`,
      {},
      { headers },
    );
  }

  getBidHistory(auctionId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/history/${auctionId}`, {
      headers: this.getHeaders(),
    });
  }

  getBidCount(auctionId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/${auctionId}`, {
      headers: this.getHeaders(),
    });
  }

  getMyParticipation(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my-participation`, {
      headers: this.getHeaders(),
    });
  }
}
