import { Component, OnInit, inject } from '@angular/core';
import { GameService } from '../../services/game.service';

@Component({
  selector: 'app-lobby',
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.css'
})
export class LobbyComponent implements OnInit {

  private readonly svc = inject(GameService)
  qr!: string;
  hostId!: string;
  numOfTeams!: number;
  teamA: string[] = ['bongo2008','xmm12345','xXfrostmanXx','vivientxy','powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bendevon','leslietangweejie','cinnamoroll','japantraveller1','ilovesingsong','lalaa-poo','muggermax','poopoopikachu']
  teamB: string[] = ['powerpuff-xoxo','987chinadoll','meowmi-xx0','kitkiat','bongo2008','xmm12345','xXfrostmanXx','vivientxy','bendevon','ilovesingsong','lalaa-poo','muggermax','poopoopikachu','leslietangweejie','cinnamoroll','japantraveller1']
  teamC: string[] = ['username3','username3','username3','username3','username3','username3','username3','username3']
  teamD: string[] = ['username4','username4','username4','username4','username4','username4','username4','username4']
  teams!: Map<number,string[]>

  ngOnInit(): void {
    this.hostId = 'a1b2c3d4';
    this.qr = 'https://api.dub.co/qr?url=https://dub.sh/8hCblH0?qr=1';
    this.numOfTeams = 2;
    for (let index = 0; index < this.numOfTeams; index++) {
      const teamList: string[] = []
      this.teams.set(index, teamList)
    }
  }

  startGame() {

  }

  remove(team: string, idx: number) {
    switch (team) {
      case 'teamA':
        this.teamA.splice(idx,1);
        break;
      case 'teamB':
        this.teamB.splice(idx,1);
        break;
      case 'teamC':
        this.teamC.splice(idx,1);
        break;
      case 'teamD':
        this.teamD.splice(idx,1);
        break;
      default:
        break;
    }
  }

}
