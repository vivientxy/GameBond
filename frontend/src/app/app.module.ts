import { NgModule, isDevMode } from '@angular/core';
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
import { HttpClientModule } from '@angular/common/http';
import { ResetPasswordComponent } from './components/user/reset-password.component';
import { ChatboxComponent } from './components/game/chatbox.component';
import { MainGameComponent } from './components/game/main-game.component';
import { HostGameComponent } from './components/host/host-game.component';
import { UserService } from './services/user.service';
import { GameService } from './services/game.service';
import { LobbyComponent } from './components/host/lobby.component';
import { JoinGameComponent } from './components/host/join-game.component';
import { WebSocketService } from './services/websocket.service';
import { GameScreenComponent } from './components/game/game-screens/game-screen.component';
import { GameScreenBComponent } from './components/game/game-screens/game-screen-b.component';
import { GameScreenCComponent } from './components/game/game-screens/game-screen-c.component';
import { GameScreenDComponent } from './components/game/game-screens/game-screen-d.component';
import { ChatboxStore } from './stores/chatbox.store';
import { AddRomComponent } from './components/game/add-rom.component';
import { MembershipComponent } from './components/membership/membership.component';
import { StripeService } from './services/stripe.service';
import { SuccessComponent } from './components/membership/success.component';
import { FailComponent } from './components/membership/fail.component';
import { rxStompServiceFactory } from './rx-stomp-service-factory';
import { RxStompService } from './rx-stomp.service';
import { ServiceWorkerModule } from '@angular/service-worker';

@NgModule({
  declarations: [
    AppComponent,
    SelectorComponent,
    LoginComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    GameScreenComponent,
    GameScreenBComponent,
    GameScreenCComponent,
    GameScreenDComponent,
    MainGameComponent,
    ChatboxComponent,
    HostGameComponent,
    LobbyComponent,
    JoinGameComponent,
    AddRomComponent,
    MembershipComponent,
    SuccessComponent,
    FailComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    NgMaterialsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      registrationStrategy: 'registerWhenStable:30000'
    }),
  ],
  providers: [
    provideAnimationsAsync(),
    UserService,
    GameService,
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
    },
    WebSocketService,
    ChatboxStore,
    StripeService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
