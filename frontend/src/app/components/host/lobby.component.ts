import { Component, OnInit, inject } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { WebSocketService } from '../../services/websocket.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.css'
})
export class LobbyComponent implements OnInit {

  private readonly gameSvc = inject(GameService)
  private readonly webSocketSvc = inject(WebSocketService)
  private readonly http = inject(HttpClient)
  private readonly router = inject(Router)
  qr$!: Observable<any>;
  hostId!: string;
  gameId!: string;
  numOfTeams!: number;
  teams: Map<string,string[]> = new Map();
  // console.log('>>> teamA:', this.teams.get('A'))


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

    this.qr$ = this.gameSvc.generateQrCode(this.hostId);

    const alphabetList = ['A', 'B', 'C', 'D'];
    for (let index = 0; index < this.numOfTeams; index++) {
      const teamList: string[] = []
      this.teams.set(alphabetList[index], teamList)
    }

    // WEBSOCKET - subscribe to one websocket topic ("hostid") here
    this.webSocketSvc.subscribe(`/topic/${hostId}`, (): any => {
      this.http.post("/api/get-team-members", hostId).subscribe(
        resp => {
          console.log('>>> WEBSOCKET RESP:', resp);
          this.teams = resp as Map<string,string[]>;
        }
      )
    })
  }

  startGame() {
    localStorage.setItem("gameStarted", "true")
    this.router.navigate(['/game'])
  }

  remove(team: string, idx: number) {
      this.teams.get(team)?.splice(idx,1);
  }

}
