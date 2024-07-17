import { Component, OnInit, inject } from '@angular/core';
import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';
import { Membership, MEMBERSHIPS } from '../../models/membership.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrl: './success.component.css'
})
export class SuccessComponent implements OnInit {

  private readonly userSvc = inject(UserService)
  private readonly activatedRoute = inject(ActivatedRoute)
  user!: User;
  membership!: Membership | undefined;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    if (!user)
      return;
    this.user = user

    let tier = this.activatedRoute.snapshot.params['tier'];
    this.membership = MEMBERSHIPS.at(tier);
  }

}
