import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GameService } from '../../services/game.service';
import { GameRom } from '../../models/gamerom.model';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { Title } from '@angular/platform-browser';
import { firstValueFrom } from 'rxjs';
import { HostGame } from '../../models/hostgame.model';

@Component({
  selector: 'app-host-game',
  templateUrl: './host-game.component.html',
  styleUrl: './host-game.component.css'
})
export class HostGameComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private readonly gameSvc = inject(GameService)
  private readonly userSvc = inject(UserService)
  private readonly router = inject(Router)
  hostForm!: FormGroup;
  gameList: GameRom[] = []
  user!: User;

  constructor(private titleService:Title) {
    this.titleService.setTitle("Host a Game! | GameBond");
  }

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    if (user)
      this.user = user

    this.hostForm = this.fb.group({
      numOfTeams: this.fb.control(null,[Validators.required]),
      game: this.fb.control(null,[Validators.required])
    })

    this.gameSvc.getAllGameDetails(this.user.username)
      .then(resp => {
        this.gameList = resp;
      })
  }

  processHostGame() {
    let numOfTeams = this.hostForm.controls['numOfTeams'].value;
    let gameId = this.hostForm.controls['game'].value;

    this.gameSvc.checkMonthlyLimit(this.user.username)
      .then(() => {
        this.gameSvc.startLobby(numOfTeams, gameId);
        let game = sessionStorage.getItem('game');
        let hostgame!: HostGame;
        if (game)
          hostgame = JSON.parse(game) as HostGame
        return this.gameSvc.addHostGameToUser(this.user.username, hostgame.hostId, hostgame.gameId, hostgame.numOfTeams)
      })
      .then(() => {
        this.router.navigate(['/lobby'])})
      .catch(err => {
        console.error(err.error);
        alert(err.error);
        this.router.navigate(['/membership']);
        return;
      }
    )
  }

}
