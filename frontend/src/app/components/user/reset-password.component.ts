import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from '../../models/user.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly svc = inject(UserService);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  resetForm!: FormGroup;
  resetId!: string;
  user!: User;
  isValid = true;

  constructor(private titleService:Title) {
    this.titleService.setTitle("Reset your password | GameBond");
  }
  
  ngOnInit(): void {
    this.resetForm = this.fb.group({
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(32)]),
      passwordConfirm: this.fb.control<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(32)])
    })

    this.activatedRoute.params.subscribe(params => {
      this.resetId = params['resetId'];
      this.svc.validateResetId(this.resetId)
        .then(resp => {this.user = resp as User})
        .catch(err => {this.isValid = false})
    })  
  }

  processReset() {
    if (this.resetForm.invalid)
      return

    const p1 = this.resetForm.controls['password'].value
    const p2 = this.resetForm.controls['passwordConfirm'].value
    
    if (p1 !== p2) {
      this.resetForm.get('passwordConfirm')?.setErrors({ passwordNotMatch: true });
      return
    }

    this.user.password = p1
    this.svc.updatePassword(this.user)
    alert("Password has been reset!")
    this.router.navigate(['/login'])
  }

  // password:
  hide1 = true;
  hide2 = true;
  clickEvent(event: MouseEvent, hide: number) {
    if (hide == 1)
      this.hide1 = !this.hide1;
    if (hide == 2)
      this.hide2 = !this.hide2;
    event.stopPropagation();
  }

}