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

    const teamsList = ['Team A', 'Team B', 'Team C', 'Team D'];
    for (let index = 0; index < this.numOfTeams; index++) {
      const teamList: string[] = []
      this.teams.set(teamsList[index], teamList)
    }

    // WEBSOCKET - subscribe to one websocket topic ("hostid") here
    this.webSocketSvc.subscribe(`/topic/${hostId}`, (): any => {
      this.http.post("/api/get-team-members", hostId).subscribe(
        resp => {
          this.teams = this.convertToMap(resp as {[key: string]: string[]});
          console.log('>>> this.teams:', this.teams);
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
      // TODO: SEND TO SPRINGBOOT TOO -- to send msg on telebot
  }

  private convertToMap(response: {[key: string]: string[]}): Map<string, string[]> {
    const map = new Map<string, string[]>();
    Object.keys(response).forEach(key => {
      map.set(key, response[key]);
    });
    return map;
  }

}
