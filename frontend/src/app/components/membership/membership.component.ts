import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
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
  membershipForm!: FormGroup;
  user!: User;
  tiers = MEMBERSHIPS;
  isNewMember: boolean = true;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    if (!user) {
      return;
    }
    this.user = user

    this.stripeSvc.checkNewMember(user.email)
      .then(resp => {this.isNewMember = resp.isNewMember}
    )

    this.membershipForm = this.fb.group({
      tier: this.fb.control(0,[Validators.required]),
    })
  }

  upgradeMembership() {
    let membership: Membership = this.membershipForm.controls['tier'].value;
    this.stripeSvc.createPaymentSession(membership.tier, this.user.email)
      .then(resp => {
        window.location.href = resp.checkoutUrl;
      })
  }
}
