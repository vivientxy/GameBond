import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { WebSocketService } from '../../services/websocket.service';
import { HttpClient } from '@angular/common/http';
import { HostGame } from '../../models/hostgame.model';

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.css'
})
export class LobbyComponent implements OnInit, OnDestroy {

  private readonly gameSvc = inject(GameService)
  private readonly webSocketSvc = inject(WebSocketService)
  private readonly http = inject(HttpClient)
  private readonly router = inject(Router)
  qr$!: Promise<any>;
  game!: HostGame;
  teams: Map<string,string[]> = new Map();

  ngOnInit(): void {
    let game = this.gameSvc.getGame();
    if (game)
      this.game = game;
    
    this.qr$ = this.gameSvc.generateQrCode(this.game.hostId)

    const teamsList = ['Team A', 'Team B', 'Team C', 'Team D'];
    for (let index = 0; index < this.game.numOfTeams; index++) {
      this.teams.set(teamsList[index], [])
    }

    // initial call for existing team members if navigated back from main-game screen
    firstValueFrom(this.http.post("/api/get-team-members", this.game.hostId))
      .then(resp => {this.teams = this.convertToMap(resp as {[key: string]: string[]})}
    )

    this.webSocketSvc.subscribe(`/topic/${this.game.hostId}`, (): any => {
      console.log('>>> subscribing to websocket:', this.game.hostId)
      this.http.post("/api/get-team-members", this.game.hostId).subscribe(
        resp => {
          this.teams = this.convertToMap(resp as {[key: string]: string[]})
          console.log('>>> this.teams:', this.teams)
        }
      )
    })
  }

  ngOnDestroy(): void {
    this.webSocketSvc.unsubscribe(`/topic/${this.game.hostId}`)
  }

  startGame() {
    this.router.navigate(['/game'])
  }

  remove(team: string, idx: number) {
      // let member = this.teams.get(team)?.splice(idx,1).at(0);
      // console.log(">>> removed person:", member)
      // TODO: SEND TO SPRINGBOOT TOO -- to send msg on telebot
      // if (member)
      //   this.webSocketSvc.sendMessage(`/topic/${this.game.hostId}`, member)
  }

  private convertToMap(response: {[key: string]: string[]}): Map<string, string[]> {
    const map = new Map<string, string[]>()
    Object.keys(response).forEach(key => {
      map.set(key, response[key])
    })
    return map
  }

}
