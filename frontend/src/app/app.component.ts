import { Component, OnInit, inject } from '@angular/core';
import { UserService } from './services/user.service';
import { GameService } from './services/game.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  private readonly userSvc = inject(UserService)
  private readonly gameSvc = inject(GameService)
  isLoggedIn = false;
  isGameStarted = false;

  ngOnInit(): void {
    // problem -- this page only init once. need change to redirect
    // this.isLoggedIn = this.userSvc.validateLoggedIn();
    // this.isGameStarted = this.gameSvc.isGameStarted();

  }
  
}
