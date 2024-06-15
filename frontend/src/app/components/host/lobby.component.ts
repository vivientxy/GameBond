import { Component, OnInit, inject } from '@angular/core';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.css'
})
export class LobbyComponent implements OnInit {

  private readonly svc = inject(GameService)
  private readonly router = inject(Router)
  qr!: string;
  hostId!: string;
  gameId!: string;
  numOfTeams!: number;
  // teamA: string[] = ['bongo2008','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu']
  // teamB: string[] = ['powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bongo2008','xmm12345','xXfrostmanXx','vivientxy','bendevon','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','leslietangweejie','cinnamoroll','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1']
  // teamC: string[] = ['xmm12345','xXfrostmanXx','vivientxy','bendevon','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bongo2008','xmm12345','xXfrostmanXx','vivientxy','bendevon','ilovesingsong','ilovesingsong','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bongo2008','xmm12345','xXfrostmanXx','vivientxy','bendevon','ilovesingsong','username3','username3','ilovesingsong','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bongo2008','xmm12345','xXfrostmanXx','vivientxy','bendevon','ilovesingsong','username3','username3','username3','username3','username3','username3','username3']
  // teamD: string[] = ['username4','username4','username4','username4','username4','username4','username4','username4']
  teams: Map<string,string[]> = new Map();

  ngOnInit(): void {
    let hostId = localStorage.getItem("hostId");
    let numOfTeams = localStorage.getItem("numOfTeams");
    let gameId = localStorage.getItem("gameId");
    let qr = localStorage.getItem("qr");
    if (!hostId || !numOfTeams || !gameId || !qr) {
      this.router.navigate(['/'])
      return;
    }
    this.hostId = hostId as string;
    this.numOfTeams = Number(numOfTeams as string);
    this.gameId = gameId as string;
    this.qr = qr as string;

    const alphabetList = ['A', 'B', 'C', 'D'];
    for (let index = 0; index < this.numOfTeams; index++) {
      const teamList: string[] = []
      this.teams.set(alphabetList[index], teamList)
    }

    console.log('>>> teams:', this.teams)
    console.log('>>> teamA:', this.teams.get('A'))
    console.log('>>> teamB:', this.teams.get('B'))
    console.log('>>> teamC:', this.teams.get('C'))
    console.log('>>> teamD:', this.teams.get('D'))

  }

  startGame() {
    localStorage.setItem("gameStarted", "true")
  }

  remove(team: string, idx: number) {
      this.teams.get(team)?.splice(idx,1);
  }

}
