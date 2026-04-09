import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WatchlistService {
  private apiUrl = 'http://localhost:8080/api/watchlist';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  toggleWatchlist(auctionId: number): Observable<string> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post(
      `${this.apiUrl}/toggle/${auctionId}`,
      {},
      {
        headers,
        responseType: 'text',
      },
    );
  }

  getSavedAuctions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/my-list`, {
      headers: this.getHeaders(),
    });
  }
}
