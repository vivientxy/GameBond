import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable()
export class WebSocketService {

  socket = new SockJS('http:localhost:8080/sba-websocket');
  stompClient = Stomp.over(this.socket);

  subscribe(topic: string, callback: (message: any) => void): void {
    const connected: boolean = this.stompClient.connected;
    if (connected) {
      this.subscribeToTopic(topic, callback);
      return;
    }
  
    // if stomp client is not connected - connect and subscribe to topic
    this.stompClient.connect({}, () => {
      this.subscribeToTopic(topic, callback);
    });
  }  

  private subscribeToTopic(topic: string, callback: (message: any) => void): void {
    this.stompClient.subscribe(topic, (message: { body: string; }) => {
      callback(message.body);
    });
  }

  disconnect() {
    if (this.stompClient.connected) {
      this.stompClient.disconnect(() => {
        console.log("Websocket is disconnected");
      });
    }
  }

}
