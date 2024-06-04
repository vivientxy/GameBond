import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { User } from './user.model';
import { Observable, firstValueFrom, map } from 'rxjs';

@Injectable()
export class UserService {

  private http = inject(HttpClient);

  registerUser(user: User): Observable<any> {
    return this.http.post<any>('/api/register', user)
        .pipe(result => {return result})
  }

  loginUser(user: User): Observable<any> {
    return this.http.post<any>('/api/login', user)
        .pipe(result => {return result})
  }

}
