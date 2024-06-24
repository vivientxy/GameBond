import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { Gameboy } from 'gameboy-emulator';
import { GameService } from '../../../services/game.service';
import { firstValueFrom, tap, timer } from 'rxjs';
import { WebSocketService } from '../../../services/websocket.service';
import { GameStore } from '../../../stores/game.store';
import { HostGame } from '../../../models/hostgame.model';

@Component({
  selector: 'app-game-screen',
  templateUrl: './game-screen.component.html',
  styleUrl: './game-screen.component.css'
})
export class GameScreenComponent implements OnInit {

  private readonly gameSvc = inject(GameService);
  private readonly webSocketSvc = inject(WebSocketService);
  private readonly gameStore = inject(GameStore)
  game!: HostGame;
  gameboy = new Gameboy();
  
  @ViewChild('gameCanvasA', { static: true }) gameCanvasA!: ElementRef<HTMLCanvasElement>;

  ngOnInit(): void {
    this.gameStore.getGame.subscribe(resp => {this.game = resp as HostGame})

    // set up and run gameboy:
    const context = this.gameCanvasA.nativeElement.getContext('2d');
    if (context) {
      firstValueFrom(this.gameSvc.getGameROM(this.game.gameId))
        .then(resp => fetch(resp.romUrl))
        .then(file => file.arrayBuffer())
        .then(arrBuffer => {
          this.gameboy.loadGame(arrBuffer);
          this.gameboy.onFrameFinished((imageData: ImageData) => {
            context.putImageData(imageData, 0, 0);
          });
          this.gameboy.run();
        })
        .catch(err => {
          console.error("Error loading ROM", err);
        });
    }

    // subscribe to websocket topic and inputs:
    this.webSocketSvc.subscribe(`/topic/${this.game.hostId}/TeamA`, (message: any) => {
      this.processInput(JSON.parse(message).message);
      this.gameSvc.sendToChatBox('A', JSON.parse(message).username + ': ' + JSON.parse(message).message)
    })
  }

  processInput(input: string) {
    this.deactivateInputs();
    switch (input) {
      case 'A':
        this.gameboy.input.isPressingA = true;
        break;
      case 'B':
        this.gameboy.input.isPressingB = true;
        break;
      case 'LEFT':
        this.gameboy.input.isPressingLeft = true;
        break;
      case 'RIGHT':
        this.gameboy.input.isPressingRight= true;
        break;
      case 'DOWN':
        this.gameboy.input.isPressingDown = true;
        break;
      case 'UP':
        this.gameboy.input.isPressingUp = true;
        break;
      case 'START':
        this.gameboy.input.isPressingStart = true;
        break;
      case 'SELECT':
        this.gameboy.input.isPressingSelect = true;
        break;
      default:
        break;
    }

    timer(100).pipe(tap(() => this.deactivateInputs())).subscribe();
  }

  deactivateInputs() {
    this.gameboy.input.isPressingA = false;
    this.gameboy.input.isPressingB = false;
    this.gameboy.input.isPressingLeft = false;
    this.gameboy.input.isPressingRight = false;
    this.gameboy.input.isPressingDown = false;
    this.gameboy.input.isPressingUp = false;
    this.gameboy.input.isPressingStart = false;
    this.gameboy.input.isPressingSelect = false;
  }


}
