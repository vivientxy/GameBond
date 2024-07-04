import { Component, inject } from '@angular/core';
import { User } from '../../models/user.model';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-fail',
  templateUrl: './fail.component.html',
  styleUrl: './fail.component.css'
})
export class FailComponent {
  
  private readonly userSvc = inject(UserService)
  user!: User;

  ngOnInit(): void {
    let user = this.userSvc.getUser();
    if (user)
      this.user = user    
  }

}
