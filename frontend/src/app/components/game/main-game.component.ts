import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrl: './main-game.component.css'
})
export class MainGameComponent implements OnInit {

  private readonly router = inject(Router)
  hostId!: string;
  gameId!: string;
  numOfTeams!: number;

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


  }

  back() {
    localStorage.removeItem("gameStarted");
    this.router.navigate(['/lobby'])
  }
}
