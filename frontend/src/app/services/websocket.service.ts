import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable()
export class WebSocketService {

  private stompClient: any | null = null;
  private socket: WebSocket | null = null;

  connect(): void {
    this.socket = new SockJS('http://localhost:8080/sba-websocket');
    this.stompClient = Stomp.over(this.socket);

    this.stompClient.connect({}, () => {
      console.log('Web Socket Opened...')
    }, this.onError);
  }

  subscribe(topic: string, callback: (message: any) => void): void {
    if (this.stompClient && this.stompClient.connected) {
      this.subscribeToTopic(topic, callback)
    } else if (this.stompClient) {
      this.stompClient.connect({}, () => {
        this.subscribeToTopic(topic, callback)
      }, this.onError);
    } else {
      console.error('Stomp client is not initialized.');
    }
  }

  private subscribeToTopic(topic: string, callback: (message: any) => void): void {
    if (this.stompClient) {
      this.stompClient.subscribe(topic, (message: { body: string }) => {
        callback(message.body)
      });
    } else {
      console.error('Cannot subscribe to topic because stompClient is null.')
    }
  }

  disconnect(): void {
    if (this.stompClient) {
      console.log('Disconnecting Web Socket...')
      this.stompClient.disconnect(() => {
        console.log('Websocket is disconnected')
      });
      this.stompClient = null;
      this.socket = null;
    }
  }

  private onError(error: any): void {
    console.error('Error in WebSocket connection:', error)
  }

}
