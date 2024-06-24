import { Component, OnInit, inject } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { WebSocketService } from '../../services/websocket.service';
import { HttpClient } from '@angular/common/http';
import { GameStore } from '../../stores/game.store';
import { HostGame } from '../../models/hostgame.model';

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.css'
})
export class LobbyComponent implements OnInit {

  private readonly gameSvc = inject(GameService)
  private readonly gameStore = inject(GameStore)
  private readonly webSocketSvc = inject(WebSocketService)
  private readonly http = inject(HttpClient)
  private readonly router = inject(Router)
  qr$!: Observable<any>;
  game!: HostGame;
  teams: Map<string,string[]> = new Map();

  ngOnInit(): void {
    if (!this.gameStore.isValidGame) {
      this.router.navigate(['/'])
      return;
    } 

    this.gameStore.getGame.subscribe(resp => {this.game = resp as HostGame})
    this.qr$ = this.gameSvc.generateQrCode(this.game.hostId);

    const teamsList = ['Team A', 'Team B', 'Team C', 'Team D'];
    for (let index = 0; index < this.game.numOfTeams; index++) {
      const teamList: string[] = []
      this.teams.set(teamsList[index], teamList)
    }

    // WEBSOCKET - subscribe to one websocket topic ("hostid") here
    this.webSocketSvc.subscribe(`/topic/${this.game.hostId}`, (): any => {
      console.log('>>> subscribing to websocket:', this.game.hostId);
      this.http.post("/api/get-team-members", this.game.hostId).subscribe(
        resp => {
          this.teams = this.convertToMap(resp as {[key: string]: string[]});
          console.log('>>> this.teams:', this.teams);
        }
      )
    })
  }

  startGame() {
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
