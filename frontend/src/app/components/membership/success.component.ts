import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
  user!: User;
  membership!: Membership | undefined;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    if (!user)
      return;
    this.user = user

    this.stripeSvc.getMembership(user.email).subscribe({
      next: resp => {
        console.log('>>> getMembership resp:', resp)
        this.membership = MEMBERSHIPS.at(resp.tier);
      },
      error: err => {console.error('>>> error:', err)}
    })
  }

}
