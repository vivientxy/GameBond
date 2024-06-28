import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GameService } from '../../services/game.service';
import { GameRom } from '../../models/gamerom.model';
import { Router } from '@angular/router';
import { GameStore } from '../../stores/game.store';
import { HostGame } from '../../models/hostgame.model';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-host-game',
  templateUrl: './host-game.component.html',
  styleUrl: './host-game.component.css'
})
export class HostGameComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private readonly gameStore = inject(GameStore)
  private readonly gameSvc = inject(GameService)
  private readonly userSvc = inject(UserService)
  private readonly router = inject(Router)
  hostForm!: FormGroup;
  gameList: GameRom[] = []
  user!: User;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    if (user) {
      this.user = user
    } else {
      this.router.navigate(['/login'])
      return
    }

    this.hostForm = this.fb.group({
      numOfTeams: this.fb.control(null,[Validators.required]),
      game: this.fb.control(null,[Validators.required])
    })

    this.gameSvc.getAllGameDetails(this.user.username).subscribe(resp => {
      this.gameList = resp;
    })
  }

  processHostGame() {
    let numOfTeams = this.hostForm.controls['numOfTeams'].value;
    let gameId = this.hostForm.controls['game'].value;

    // this.gameSvc.startLobby(numOfTeams, gameId);

    this.gameStore.hostNewGame({gameId: gameId, numOfTeams: numOfTeams} as HostGame);
    this.router.navigate(['/lobby'])
  }

}
