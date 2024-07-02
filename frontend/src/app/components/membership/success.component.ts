import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../models/user.model';
import { StripeService } from '../../services/stripe.service';
import { UserService } from '../../services/user.service';
import { Membership, MEMBERSHIPS } from '../../models/membership.model';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrl: './success.component.css'
})
export class SuccessComponent implements OnInit {

  private readonly userSvc = inject(UserService)
  private readonly stripeSvc = inject(StripeService)
  private readonly router = inject(Router)
  user!: User;
  membership!: Membership | undefined;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    let uuid = sessionStorage.getItem('uuid');

    if (user && uuid) {
      this.user = user
    } else {
      this.router.navigate(['/'])
      return
    }

    // update SQL membership
    this.stripeSvc.upgradeMembership(uuid, user.email).subscribe({
      next: resp => {
        this.membership = MEMBERSHIPS.at(resp.tier)
        console.log('>>> membership:', this.membership)
      },
      error: err => {
        console.error(err);
        this.router.navigate(['/upgrade/fail'])
      }
    })
    
    // sessionStorage.removeItem('uuid');
  }

}
