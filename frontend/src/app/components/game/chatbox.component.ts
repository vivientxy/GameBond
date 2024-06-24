import { Component, Input, OnInit } from '@angular/core';
import { Chat } from '../../models/chatbox.model';

@Component({
  selector: 'app-chatbox',
  templateUrl: './chatbox.component.html',
  styleUrl: './chatbox.component.css'
})
export class ChatboxComponent {
  
  @Input() chats: Chat[] = [];

}
