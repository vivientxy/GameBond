import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SelectorComponent } from './components/main/selector.component';
import { LoginComponent } from './components/user/login.component';
import { RegisterComponent } from './components/user/register.component';
import { ForgotPasswordComponent } from './components/user/forgot-password.component';
import { ResetPasswordComponent } from './components/user/reset-password.component';
import { HostGameComponent } from './components/host/host-game.component';
import { LobbyComponent } from './components/host/lobby.component';
import { JoinGameComponent } from './components/host/join-game.component';
import { MainGameComponent } from './components/game/main-game.component';
import { AddRomComponent } from './components/game/add-rom.component';
import { MembershipComponent } from './components/membership/membership.component';
import { SuccessComponent } from './components/membership/success.component';
import { FailComponent } from './components/membership/fail.component';
import { gameGuard, loginGuard } from './route.guards';

const routes: Routes = [
  { path: '', component: SelectorComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset/:resetId', component: ResetPasswordComponent },
  { path: 'reset', component: ForgotPasswordComponent },
  { path: 'membership', component: MembershipComponent, canActivate: [loginGuard] },
  { path: 'payment/success/:tier', component: SuccessComponent, canActivate: [loginGuard] },
  { path: 'upgrade/fail', component: FailComponent, canActivate: [loginGuard] },
  { path: 'join', component: JoinGameComponent },
  { path: 'host', component: HostGameComponent, canActivate: [loginGuard] },
  { path: 'lobby', component: LobbyComponent, canActivate: [loginGuard, gameGuard] },
  { path: 'game', component: MainGameComponent, canActivate: [loginGuard, gameGuard] },
  { path: 'add-rom', component: AddRomComponent, canActivate: [loginGuard] },
  { path: '**', redirectTo: '/', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
