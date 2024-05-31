import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  registerForm!: FormGroup;

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: this.fb.control('', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
      password: this.fb.control('', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]),
      email: this.fb.control('', [Validators.required, Validators.email, Validators.maxLength(64)]),
      firstname: this.fb.control('', [Validators.maxLength(32)]),
      lastname: this.fb.control('', [Validators.maxLength(32)]),
    })
  }

  processRegistration() {
    // TODO
    if (this.registerForm.invalid) {
      return
    }
    this.registerForm.reset()
  }

}
