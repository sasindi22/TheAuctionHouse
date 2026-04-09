import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuctionService {
  private baseUrl = 'http://localhost:8080/api/auctions';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  search(keyword?: string, category?: string): Observable<any[]> {
    let url = `${this.baseUrl}/search?`;

    if (keyword) url += `keyword=${keyword}&`;
    if (category && category !== 'All') url += `category=${category}&`;

    return this.http.get<any[]>(url);
  }

  filterByPrice(min: number, max: number): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/filter/price?min=${min}&max=${max}`,
    );
  }

  filterByShipping(option: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/filter/shipping?option=${option}`,
    );
  }

  createAuction(data: any, token: string) {
    return this.http.post('http://localhost:8080/api/auctions/create', data, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  getById(id: string | number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  getMyOngoing(token: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/reports/ongoing`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  getMyAuctions(token: string): Observable<any[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<any[]>(`${this.baseUrl}/reports/my-auctions`, {
      headers,
    });
  }

  getMyHistory(token: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/reports/history`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  relistItem(auctionId: number): Observable<any> {
    const token = localStorage.getItem('token');
    return this.http.post(
      `${this.baseUrl}/${auctionId}/relist`,
      {},
      {
        headers: { Authorization: `Bearer ${token}` },
      },
    );
  }
}
