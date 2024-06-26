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
    if (!this.gameStore.isValidGame) {
      this.router.navigate(['/'])
      return;
    } 

    this.gameStore.getGame.subscribe(resp => {this.game = resp as HostGame})

    this.webSocketSvc.unsubscribe(`/topic/${this.game.hostId}`);
    this.chatStore.resetChats(true);

  }

  ngOnDestroy(): void {
    // this.chatStore
    this.webSocketSvc.disconnect();
  }

  back() {
    // localStorage.removeItem("gameStarted");
    this.router.navigate(['/lobby'])
  }

}
