import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SelectorComponent } from './components/main/selector.component';
import { LoginComponent } from './components/user/login.component';
import { RegisterComponent } from './components/user/register.component';
import { ForgotPasswordComponent } from './components/user/forgot-password.component';
import { ResetPasswordComponent } from './components/user/reset-password.component';

const routes: Routes = [
  { path: '', component: SelectorComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset/:resetId', component: ResetPasswordComponent },
  { path: 'reset', component: ForgotPasswordComponent },
  { path: '**', redirectTo: '/', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
