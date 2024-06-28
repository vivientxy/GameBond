import { Component, inject } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-selector',
  templateUrl: './selector.component.html',
  styleUrl: './selector.component.css'
})
export class SelectorComponent {

  private readonly userSvc = inject(UserService)
  private readonly router = inject(Router)

  joinGame() {
    this.router.navigate(['/join'])
  }

  hostGame() {
    if (this.userSvc.isLoggedIn())
      this.router.navigate(['/host'])
    else
      this.router.navigate(['/login'])
  }

}
