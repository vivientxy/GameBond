import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { User } from './user.model';
import { firstValueFrom, map } from 'rxjs';

@Injectable()
export class UserService {

  private http = inject(HttpClient);

  registerUser(user: User): Promise<any> {
    return firstValueFrom<string>(
      this.http.post<any>('/api/register', user)
        .pipe(result => {
            console.log('>>> angular service - register user: ', result);
            return result;
        })
    );
  }


}
