import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../../services/websocket.service';
import { GameStore } from '../../stores/game.store';
import { HostGame } from '../../models/hostgame.model';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrl: './main-game.component.css'
})
export class MainGameComponent implements OnInit, OnDestroy {

  private readonly router = inject(Router)
  private readonly gameStore = inject(GameStore)
  private readonly webSocketSvc = inject(WebSocketService)
  game!: HostGame;
  messagesA: string[] = [];
  messagesB: string[] = [];
  messagesC: string[] = [];
  messagesD: string[] = [];

  ngOnInit(): void {
    if (!this.gameStore.isValidGame) {
      this.router.navigate(['/'])
      return;
    } 

    this.gameStore.getGame.subscribe(resp => {this.game = resp as HostGame})
  }

  ngOnDestroy(): void {
    this.webSocketSvc.disconnect();
  }

  back() {
    // localStorage.removeItem("gameStarted");
    this.router.navigate(['/lobby'])
  }
}
