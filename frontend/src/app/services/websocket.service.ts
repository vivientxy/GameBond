import { Injectable } from '@angular/core';
import { WebSocketSubject } from 'rxjs/webSocket';

@Injectable()
export class WebSocketService {
  private socket$: WebSocketSubject<any>;

  constructor() {
    this.socket$ = new WebSocketSubject('ws://localhost:8080/ws');
  }

  sendMessage(msg: any) {
    this.socket$.next(msg);
  }

  getMessages() {
    return this.socket$.asObservable();
  }
}
