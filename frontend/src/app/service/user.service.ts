import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getProfile(token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/profile`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  updateProfile(userData: any, token: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/update`, userData, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }
}
