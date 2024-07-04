import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Subject, firstValueFrom } from 'rxjs';
import { User } from '../models/user.model';

@Injectable()
export class UserService {

  private readonly http = inject(HttpClient);

  registerUser(user: User): Promise<any> {
    return firstValueFrom(this.http.post<any>('/api/register', user))
  }

  loginUser(user: User): Promise<any> {
    return firstValueFrom(this.http.post<any>('/api/login', user))
  }

  resetPassword(user: User): Promise<any> {
    return firstValueFrom(this.http.post<any>('/api/reset', user))
  }

  validateResetId(resetId: string): Promise<any> {
    return firstValueFrom(this.http.get<any>(`/api/reset/${resetId}`))
  }

  updatePassword(user: User): Promise<any> {
    return firstValueFrom(this.http.put<any>('/api/reset', user))
  }

  loggedInSignal = new Subject<boolean>();

  isLoggedInAsObservable() {
    return this.loggedInSignal.asObservable();
  }

  isLoggedIn(): boolean {
    return sessionStorage.getItem('user') != null ? true : false;
  }

  logout(): void {
    this.loggedInSignal.next(false);
    sessionStorage.removeItem('user');
  }

  getUser(): User | null {
    let userString = sessionStorage.getItem('user');
    if (userString)
      return JSON.parse(userString) as User;
    return null;
  }

}
