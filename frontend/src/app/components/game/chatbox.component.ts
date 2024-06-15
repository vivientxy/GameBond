import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-chatbox',
  templateUrl: './chatbox.component.html',
  styleUrl: './chatbox.component.css'
})
export class ChatboxComponent implements OnInit {
  
  message: string = '';
  messages: string[] = [];

  constructor(private wsService: WebSocketService) {}

  ngOnInit() {
    this.wsService.getMessages().subscribe((msg) => {
      this.messages.push(msg);
    });
  }

  sendMessage() {
    this.wsService.sendMessage(this.message);
    this.message = '';
  }

}
