import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GameService } from '../../services/game.service';
import { GameDetails } from '../../models/gamedetails.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-host-game',
  templateUrl: './host-game.component.html',
  styleUrl: './host-game.component.css'
})
export class HostGameComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private readonly gameSvc = inject(GameService)
  private readonly router = inject(Router)
  hostForm!: FormGroup;
  gameList: GameDetails[] = []

  ngOnInit(): void {
    this.hostForm = this.fb.group({
      numOfTeams: this.fb.control(null,[Validators.required]),
      game: this.fb.control(null,[Validators.required])
    })
    this.gameSvc.getAllGameDetails()
      .subscribe(response => {this.gameList =  response})
  }

  processHostGame() {
    this.gameSvc.startLobby(this.hostForm.controls['numOfTeams'].value, this.hostForm.controls['game'].value);
    let hostId = this.gameSvc.generateHostId();
    localStorage.setItem("hostId", hostId);
    localStorage.setItem("numOfTeams", this.hostForm.controls['numOfTeams'].value);
    localStorage.setItem("gameId", this.hostForm.controls['game'].value);
    // this.gameSvc.generateQrCode(hostId).subscribe(
    //   QR => {localStorage.setItem("qr", QR)}
    // )

    this.router.navigate(['/lobby'])
  }

}
