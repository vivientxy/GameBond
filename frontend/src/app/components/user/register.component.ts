import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from '../../models/user.model';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { UserService } from '../../services/user.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit, OnDestroy {

  private readonly fb = inject(FormBuilder)
  private readonly router = inject(Router)
  private readonly userSvc = inject(UserService)
  registerForm!: FormGroup;
  private unsubscribe$ = new Subject();

  constructor(private titleService:Title) {
    this.titleService.setTitle("Register an Account! | GameBond");
  }
  
  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: this.fb.control<string>('', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]),
      email: this.fb.control<string>('', [Validators.required, Validators.email, Validators.maxLength(64)]),
      firstname: this.fb.control<string>('', [Validators.maxLength(32)]),
      lastname: this.fb.control<string>('', [Validators.maxLength(32)]),
    })
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next(null);
    this.unsubscribe$.complete();
  }

  processRegistration() {
    if (this.registerForm.invalid)
      return

    const user: User = {
      username: this.registerForm.value['username'],
      password: this.registerForm.value['password'],
      email: this.registerForm.value['email'],
      firstName: this.registerForm.value['firstname'],
      lastName: this.registerForm.value['lastname']
    }

    this.userSvc.registerUser(user)
      .then(registerSuccess => {this.router.navigate(['/login'])})
      .catch(err => {
        this.registerForm.patchValue(user)
        if (err.error == "Username already in use")
          this.registerForm.get('username')?.setErrors({ usernameExists: true });
        if (err.error == "Email already in use")
          this.registerForm.get('email')?.setErrors({ emailExists: true });
        this.markFormControlsAsTouched(this.registerForm)
        return
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
