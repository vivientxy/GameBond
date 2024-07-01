import { Injectable } from '@angular/core';
import { Subscription } from 'rxjs';
import { RxStompService } from '../rx-stomp.service';

@Injectable()
export class WebSocketService {

  constructor(private rxStompService: RxStompService) { }
  private subscriptions: { [topic: string]: Subscription } = {};

  subscribe(topic: string, callback: (message: any) => void): void {
    this.subscriptions[topic] = this.rxStompService.watch(topic)
      .subscribe((message: { body: string }) => {
        callback(message.body)
      })
  }

  unsubscribe(topic: string): void {
    if (this.subscriptions[topic]) {
      this.subscriptions[topic].unsubscribe();
      delete this.subscriptions[topic];
      console.log(`Unsubscribed from topic: ${topic}`);
    }
  }

  unsubscribeAll(): void {
    for (const topic in this.subscriptions)
      this.unsubscribe(topic);
  }

}
