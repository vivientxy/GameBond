import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Membership, MEMBERSHIPS } from '../../models/membership.model';
import { User } from '../../models/user.model';
import { StripeService } from '../../services/stripe.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-fail',
  templateUrl: './fail.component.html',
  styleUrl: './fail.component.css'
})
export class FailComponent {
  
  private readonly userSvc = inject(UserService)
  private readonly stripeSvc = inject(StripeService)
  private readonly router = inject(Router)
  user!: User;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    let uuid = sessionStorage.getItem('uuid');

    if (user && uuid) {
      this.user = user
    } else {
      this.router.navigate(['/'])
      return
    }
    
    sessionStorage.removeItem('uuid');
  }

}
