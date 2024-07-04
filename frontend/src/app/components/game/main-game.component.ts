import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../../services/websocket.service';
import { HostGame } from '../../models/hostgame.model';
import { ChatboxStore } from '../../stores/chatbox.store';
import { Chat } from '../../models/chatbox.model';
import { Observable } from 'rxjs';
import { GameService } from '../../services/game.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrl: './main-game.component.css'
})
export class MainGameComponent implements OnInit, OnDestroy {

  private readonly router = inject(Router)
  private readonly gameSvc = inject(GameService)
  private readonly chatStore = inject(ChatboxStore)
  private readonly webSocketSvc = inject(WebSocketService)
  game!: HostGame;
  messagesA$: Observable<Chat[]> = this.chatStore.getChats('TeamA');
  messagesB$: Observable<Chat[]> = this.chatStore.getChats('TeamB');
  messagesC$: Observable<Chat[]> = this.chatStore.getChats('TeamC');
  messagesD$: Observable<Chat[]> = this.chatStore.getChats('TeamD');

  constructor(private titleService:Title) {
    this.titleService.setTitle("Game On! | GameBond");
  }
  
  ngOnInit(): void {
    let game = this.gameSvc.getGame();
    if (game)
      this.game = game;
  }

  ngOnDestroy(): void {
    this.webSocketSvc.unsubscribeAll();
    this.chatStore.resetChats(true);
  }

  endGame() {
    this.gameSvc.endGame(this.game.hostId).subscribe(); // wipe redis data
    this.router.navigate(['/lobby'])
  }

}
