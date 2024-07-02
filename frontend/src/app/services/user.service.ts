import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, Subject, firstValueFrom } from 'rxjs';
import { User } from '../models/user.model';

@Injectable()
export class UserService {

  private readonly http = inject(HttpClient);

  registerUser(user: User): Observable<any> {
    return this.http.post<any>('/api/register', user)
        .pipe(result => {return result})
  }

  loginUser(user: User): Observable<any> {
    return this.http.post<any>('/api/login', user)
        .pipe(result => {return result})
  }

  resetPassword(user: User): Observable<any> {
    return this.http.post<any>('/api/reset', user)
        .pipe(result => {return result})
  }

  validateResetId(resetId: string): Observable<any> {
    return this.http.get<any>(`/api/reset/${resetId}`)
        .pipe(result => {return result})
  }

  updatePassword(user: User): Observable<any> {
    return this.http.put<any>('/api/reset', user)
        .pipe(result => {return result})
  }

  loggedInSignal = new Subject<boolean>();

  isLoggedInAsObservable() {
    return this.loggedInSignal.asObservable();
  }

  isLoggedIn(): boolean {
    let userString = sessionStorage.getItem('user');
    if (!userString)
      return false;
    let user = JSON.parse(userString) as User
    let isValidLogin!: boolean;
    firstValueFrom(this.http.post<any>('/api/verify-login', user))
      .then(resp => {isValidLogin = true})
      .catch(err => {isValidLogin = false})
    return isValidLogin;
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
