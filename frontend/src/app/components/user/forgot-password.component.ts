import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from '../../models/user.model';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router)
  private readonly userSvc = inject(UserService);
  forgotPasswordForm!: FormGroup;
  private lastTouchedField = '';

  constructor(private titleService:Title) {
    this.titleService.setTitle("Reset your password | GameBond");
  }
  
  ngOnInit(): void {
    this.forgotPasswordForm = this.fb.group({
      username: this.fb.control(''),
      email: this.fb.control('', [Validators.email]),
    });
  }

  processForgotPassword() {
    const user: User = {
      username: this.forgotPasswordForm.value['username'],
      password: '',
      email: this.forgotPasswordForm.value['email'],
      firstName: '',
      lastName: '',
    };
    // only take value of the last touched field -- in case user filled in both
    if (this.lastTouchedField === 'username')
      user.email = '';
    else if (this.lastTouchedField === 'email')
      user.username = '';

    this.userSvc.resetPassword(user);
    alert("Password reset link will be sent to your registered email")
    this.router.navigate(['/login']);
  }

  onFieldFocus(field: string) {
    this.lastTouchedField = field;
  }

}
