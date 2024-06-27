import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GameService } from '../../services/game.service';
import { GameRom } from '../../models/gamerom.model';
import { Router } from '@angular/router';
import { GameStore } from '../../stores/game.store';
import { HostGame } from '../../models/hostgame.model';
import { RomStore } from '../../stores/rom.store';

@Component({
  selector: 'app-host-game',
  templateUrl: './host-game.component.html',
  styleUrl: './host-game.component.css'
})
export class HostGameComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private readonly gameStore = inject(GameStore)
  private readonly romStore = inject(RomStore)
  private readonly gameSvc = inject(GameService)
  private readonly router = inject(Router)
  hostForm!: FormGroup;
  gameList: GameRom[] = []

  ngOnInit(): void {
    this.hostForm = this.fb.group({
      numOfTeams: this.fb.control(null,[Validators.required]),
      game: this.fb.control(null,[Validators.required])
    })

    this.romStore.getRoms.subscribe(roms => {
      if (roms.length > 0) {
        console.log('roms retrieved from store');
        this.gameList = roms;
      } else {
        console.log('roms retrieved from api');
        this.gameSvc.getAllGameDetails().subscribe(resp => {
          this.gameList = resp;
          this.romStore.addRoms(resp);
        })
      }
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
