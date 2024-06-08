import { Component, OnInit, inject } from '@angular/core';
import { UserService } from './services/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  private readonly svc = inject(UserService)
  isLoggedIn = false;
  isGameStarted = false;

  ngOnInit(): void {
    this.isLoggedIn = this.svc.validateLoggedIn();
  }
  
}
