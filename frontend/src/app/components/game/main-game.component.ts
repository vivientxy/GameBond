import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../../services/websocket.service';
import { GameStore } from '../../stores/game.store';
import { HostGame } from '../../models/hostgame.model';
import { ChatboxStore } from '../../stores/chatbox.store';
import { Chat } from '../../models/chatbox.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrl: './main-game.component.css'
})
export class MainGameComponent implements OnInit, OnDestroy {

  private readonly router = inject(Router)
  private readonly gameStore = inject(GameStore)
  private chatStore = inject(ChatboxStore)
  private readonly webSocketSvc = inject(WebSocketService)
  game!: HostGame;
  messagesA$: Observable<Chat[]> = this.chatStore.getChats('TeamA');
  messagesB$: Observable<Chat[]> = this.chatStore.getChats('TeamB');
  messagesC$: Observable<Chat[]> = this.chatStore.getChats('TeamC');
  messagesD$: Observable<Chat[]> = this.chatStore.getChats('TeamD');

  ngOnInit(): void {
    // this.chatStore = new ChatboxStore();

    if (!this.gameStore.isValidGame) {
      this.router.navigate(['/'])
      return;
    } 

    this.gameStore.getGame.subscribe(resp => {this.game = resp as HostGame})



  }

  ngOnDestroy(): void {
    // this.chatStore
    this.webSocketSvc.disconnect();
  }

  back() {
    // localStorage.removeItem("gameStarted");
    this.router.navigate(['/lobby'])
  }

  // addChat(team: string, chat: Chat) {
  //   switch (team) {
  //     case 'TeamA':
  //       this.chatStore.addChatTeamA(chat);
  //       break;
  //     case 'TeamB':
  //       this.chatStore.addChatTeamB(chat);        
  //       break;
  //     case 'TeamC':
  //       this.chatStore.addChatTeamC(chat);
  //       break;
  //     case 'TeamD':
  //       this.chatStore.addChatTeamD(chat);
  //       break;    
  //     default:
  //       break;
  //   }
  // }

}
