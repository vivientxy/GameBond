import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { Membership, MEMBERSHIPS } from '../../models/membership.model';
import { StripeService } from '../../services/stripe.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-membership',
  templateUrl: './membership.component.html',
  styleUrl: './membership.component.css',
})
export class MembershipComponent {

  constructor(private titleService:Title) {
    this.titleService.setTitle("Reset your password | GameBond");
  }
  
  private readonly fb = inject(FormBuilder)
  private readonly userSvc = inject(UserService)
  private readonly stripeSvc = inject(StripeService)
  private readonly router = inject(Router)
  membershipForm!: FormGroup;
  user!: User;
  tiers = MEMBERSHIPS;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    if (user) {
      this.user = user
    }
    sessionStorage.removeItem('uuid');

    this.membershipForm = this.fb.group({
      tier: this.fb.control(null,[Validators.required]),
    })
  }

  upgradeMembership() {
    let membership: Membership = this.membershipForm.controls['tier'].value;
    this.stripeSvc.createPaymentSession(membership.tier, this.user.email)
      .subscribe(resp => {
        sessionStorage.setItem('uuid', resp.uuid)
        // this.stripeSvc.redirectToCheckout(resp.sessionId);
        window.location.href = resp.checkoutUrl;
      })
  }
}
