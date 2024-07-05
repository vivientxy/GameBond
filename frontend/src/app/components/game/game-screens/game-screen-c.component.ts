import { Component, ElementRef, ViewChild, inject } from '@angular/core';
import { Gameboy } from 'gameboy-emulatorC';
import { tap, timer } from 'rxjs';
import { GameService } from '../../../services/game.service';
import { WebSocketService } from '../../../services/websocket.service';
import { HostGame } from '../../../models/hostgame.model';
import { ChatboxStore } from '../../../stores/chatbox.store';
import { Chat } from '../../../models/chatbox.model';
import { GameboyService } from '../../../services/gameboy.service';

@Component({
  selector: 'app-game-screen-c',
  templateUrl: './game-screen-c.component.html',
  styleUrl: './game-screen-c.component.css'
})
export class GameScreenCComponent {
  
  private readonly gameSvc = inject(GameService);
  private readonly gbSvc = inject(GameboyService);
  private readonly webSocketSvc = inject(WebSocketService);
  private readonly chatStore = inject(ChatboxStore)
  game!: HostGame;
  gameboy = new Gameboy();
  
  @ViewChild('gameCanvasC', { static: true }) gameCanvas!: ElementRef<HTMLCanvasElement>;

  ngOnInit(): void {
    let game = this.gameSvc.getGame();
    if (game) this.game = game;

    // set up and run gameboy:    
    const context = this.gameCanvas.nativeElement.getContext('2d');
    if (context) {
      this.gameSvc.getGameROM(this.game.gameId)
        .then(resp => fetch(resp.romUrl))
        .then(file => file.arrayBuffer())
        .then(arrBuffer => {
          this.gameboy.loadGame(arrBuffer)
          this.gameboy.onFrameFinished((imageData: ImageData) => {context.putImageData(imageData, 0, 0)})
          this.gameboy.run()
        })
        .catch(err => {console.error("Error loading ROM", err)})
    }

    // subscribe to websocket topic and inputs:
    this.webSocketSvc.subscribe(`/topic/${this.game.hostId}/TeamA`, (message: any) => {
      this.gbSvc.processInput(JSON.parse(message).message, this.gameboy);
      this.chatStore.addChat({ team: 'TeamA', chat: JSON.parse(message) as Chat});
    })
  }
}
