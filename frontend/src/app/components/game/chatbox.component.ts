import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-chatbox',
  templateUrl: './chatbox.component.html',
  styleUrl: './chatbox.component.css'
})
export class ChatboxComponent implements OnInit {
  
  message: string = '';
  messages: string[] = ["vivtxy: A",
                        "kitkiat: B",
                        "bendevon: UP",
                        "leslietangweejie: UP",
                        "kitkiat: SELECT",
                        "vivtxy: B",
                        "bendevon: START",
                        "leslietangweejie: L",
                        "kitkiat: A",
                        "leslietangweejie: A",
                        "vivtxy: UP",
                        "username: UP",
                        "kitkiat: SELECT",
                        "vivtxy: B",
                        "bendevon: START",
                        "username: L",
                        "username: A",
                        "username: UP",
                        "username: UP",
                        "username: SELECT",
                        "username: B",
                        "username: START"];

  

  constructor(private wsService: WebSocketService) {}

  ngOnInit() {
    // this.wsService.getMessages().subscribe((msg) => {
    //   this.messages.push(msg);
    // });
  }

  sendMessage() {
    // this.wsService.sendMessage(this.message);
    // this.message = '';
  }

}
