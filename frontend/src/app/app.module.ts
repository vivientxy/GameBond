import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgMaterialsModule } from './ng-materials.module';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { SelectorComponent } from './components/main/selector.component';
import { LoginComponent } from './components/user/login.component';
import { RegisterComponent } from './components/user/register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ForgotPasswordComponent } from './components/user/forgot-password.component';
import { UserService } from './components/user/user.service';
import { HttpClientModule } from '@angular/common/http';
import { ResetPasswordComponent } from './components/user/reset-password.component';
import { GameScreenComponent } from './components/game/game-screen.component';
import { MainComponent } from './components/main/main.component';
import { ChatboxComponent } from './components/game/chatbox.component';
import { MainGameComponent } from './components/game/main-game.component';
import { HostGameComponent } from './components/host/host-game.component';

@NgModule({
  declarations: [
    AppComponent,
    SelectorComponent,
    LoginComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    GameScreenComponent,
    MainGameComponent,
    MainComponent,
    ChatboxComponent,
    HostGameComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    NgMaterialsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    provideAnimationsAsync(),
    UserService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
