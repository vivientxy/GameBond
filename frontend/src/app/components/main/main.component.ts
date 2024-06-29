import { Component, OnInit, inject } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit {
  title = '👾 GameBond 🎮';

  private readonly userSvc = inject(UserService)
  private readonly router = inject(Router)
  isLoggedIn: boolean = this.userSvc.isLoggedIn();

  ngOnInit(): void {
      this.userSvc.isLoggedInAsObservable().subscribe(bool => this.isLoggedIn = bool)
  }

  logout() {
    this.userSvc.logout()
    alert("Logout successful")
    this.router.navigate(['/login'])
  }

}
