import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import { merge } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  loginForm!: FormGroup;

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: this.fb.control<string>('', [Validators.required]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  processLogin() {
    // TODO
    if (this.loginForm.invalid) {
      return
    }
    this.loginForm.reset()
  }

  // password:
  hide = true;
  clickEvent(event: MouseEvent) {
    this.hide = !this.hide;
    event.stopPropagation();
  }

}
