import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { Gameboy } from 'gameboy-emulatorB';
import { firstValueFrom, tap, timer } from 'rxjs';
import { GameService } from '../../../services/game.service';
import { WebSocketService } from '../../../services/websocket.service';
import { HostGame } from '../../../models/hostgame.model';
import { GameStore } from '../../../stores/game.store';

@Component({
  selector: 'app-game-screen-b',
  templateUrl: './game-screen-b.component.html',
  styleUrl: './game-screen-b.component.css'
})
export class GameScreenBComponent implements OnInit {

  private readonly gameSvc = inject(GameService);
  private readonly webSocketSvc = inject(WebSocketService);
  private readonly gameStore = inject(GameStore)
  game!: HostGame;
  gameboy = new Gameboy();

  @ViewChild('gameCanvasB', { static: true }) gameCanvasB!: ElementRef<HTMLCanvasElement>;

  ngOnInit(): void {
    this.gameStore.getGame.subscribe(resp => {this.game = resp as HostGame})

    // set up and run gameboy:
    const context = this.gameCanvasB.nativeElement.getContext('2d');
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
    this.webSocketSvc.subscribe(`/topic/${this.game.hostId}/TeamB`, (message: any) => {
      this.processInput(message);
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
