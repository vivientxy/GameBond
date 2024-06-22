import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrl: './main-game.component.css'
})
export class MainGameComponent implements OnInit, OnDestroy {

  private readonly router = inject(Router)
  private readonly webSocketSvc = inject(WebSocketService)
  hostId!: string;
  gameId!: string;
  numOfTeams!: number;

  ngOnInit(): void {
    let hostId = localStorage.getItem("hostId");
    let numOfTeams = localStorage.getItem("numOfTeams");
    let gameId = localStorage.getItem("gameId");
    this.hostId = hostId as string;
    this.numOfTeams = Number(numOfTeams as string);
    this.gameId = gameId as string;

    if (!hostId || !numOfTeams || !gameId ) {
      this.router.navigate(['/'])
      return;
    }

  }

  ngOnDestroy(): void {
    this.webSocketSvc.disconnect();
  }

  back() {
    localStorage.removeItem("gameStarted");
    this.router.navigate(['/lobby'])
  }
}
