import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main-game',
  templateUrl: './main-game.component.html',
  styleUrl: './main-game.component.css'
})
export class MainGameComponent {

  private readonly router = inject(Router)

  back() {
    localStorage.removeItem("gameStarted");
    this.router.navigate(['/lobby'])
  }
}
