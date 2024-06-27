import { Injectable } from '@angular/core';
import { Subscription } from 'rxjs';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable()
export class WebSocketService {

  private stompClient: any | null = null;
  private socket: WebSocket | null = null;
  private subscriptions: { [topic: string]: Subscription } = {};

  connect(): void {
    this.socket = new SockJS('http://localhost:8080/sba-websocket');
    this.stompClient = Stomp.over(this.socket);
  }

  subscribe(topic: string, callback: (message: any) => void): void {
    if (!this.stompClient) {
      console.error('Stomp client is not initialised');
      return;
    }
    if (!this.stompClient.connected) {
      this.stompClient.connect({}, () => {
        this.subscribeToTopic(topic, callback)
      })
    } else {
      this.subscribeToTopic(topic, callback)
    }
  }

  private subscribeToTopic(topic: string, callback: (message: any) => void): void {
    this.subscriptions[topic] = this.stompClient.subscribe(topic, (message: { body: string }) => {
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

  disconnect(): void {
    if (this.stompClient) {
      this.unsubscribeAll();
      this.stompClient.disconnect();
      this.stompClient = null;
      this.socket = null;
    }
  }

}
