import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { User } from './user.model';
import { UserService } from './user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit, OnDestroy {

  private readonly fb = inject(FormBuilder)
  private readonly userSvc = inject(UserService)
  private readonly router = inject(Router)
  loginForm!: FormGroup;
  private unsubscribe$ = new Subject();

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: this.fb.control<string>('', [Validators.required]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next(null);
    this.unsubscribe$.complete();
  }

  processLogin() {
    if (this.loginForm.invalid)
      return

    const user: User = {
      username: this.loginForm.value['username'],
      password: this.loginForm.value['password'],
      email: '',
      firstName: '',
      lastName: ''
    }

    this.userSvc.loginUser(user)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: loginSuccess => {
          // TODO: add cookie/auth token to parent to indicate user is logged in
          this.router.navigate(['/'])
        },
        error: err => {
          this.loginForm.patchValue(user)
          if (err.error == "Username not found")
            this.loginForm.get('username')?.setErrors({ usernameNotExists: true });
          if (err.error == "Wrong password")
            this.loginForm.get('password')?.setErrors({ passwordWrong: true });
          this.markFormControlsAsTouched(this.loginForm)
          return
        }
      })
  }

  private markFormControlsAsTouched(form: FormGroup) {
    Object.values(form.controls).forEach(control => {
      control.markAsTouched();
      control.markAsDirty();
    });
  }

  // password:
  hide = true;
  clickEvent(event: MouseEvent) {
    this.hide = !this.hide;
    event.stopPropagation();
  }

}
