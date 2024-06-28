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

const routes: Routes = [
  { path: '', component: SelectorComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'reset/:resetId', component: ResetPasswordComponent },
  { path: 'reset', component: ForgotPasswordComponent },
  { path: 'join', component: JoinGameComponent },
  { path: 'host', component: HostGameComponent },
  { path: 'lobby', component: LobbyComponent },
  { path: 'game', component: MainGameComponent },
  { path: 'add-rom', component: AddRomComponent },
  { path: '**', redirectTo: '/', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
