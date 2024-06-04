import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from './user.model';
import { UserService } from './user.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private userSvc = inject(UserService)
  registerForm!: FormGroup;

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      username: this.fb.control<string>('', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]),
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]),
      email: this.fb.control<string>('', [Validators.required, Validators.email, Validators.maxLength(64)]),
      firstname: this.fb.control<string>('', [Validators.maxLength(32)]),
      lastname: this.fb.control<string>('', [Validators.maxLength(32)]),
    })
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
    console.log('>>> user object:', user)
    this.userSvc.registerUser(user)
      .then(response => {console.log('>>> register component: ', response)})
    this.registerForm.reset()
  }

  // password:
  hide = true;
  clickEvent(event: MouseEvent) {
    this.hide = !this.hide;
    event.stopPropagation();
  }

}
