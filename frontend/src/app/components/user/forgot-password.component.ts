import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  forgotPasswordForm!: FormGroup

  ngOnInit(): void {
    this.forgotPasswordForm = this.fb.group({
      username: this.fb.control<string>(''),
      email: this.fb.control<string>('', [Validators.email])
    })
  }

  processForgotPassword() {
    // TODO
    if (this.forgotPasswordForm.invalid) {
      return
    }
    this.forgotPasswordForm.reset()
  }

}
