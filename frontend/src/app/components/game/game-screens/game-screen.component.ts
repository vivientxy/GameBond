import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { Gameboy } from 'gameboy-emulator';
import { GameService } from '../../../services/game.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-game-screen',
  templateUrl: './game-screen.component.html',
  styleUrl: './game-screen.component.css'
})
export class GameScreenComponent implements OnInit {

  private readonly svc = inject(GameService);
  gameboy = new Gameboy();
  gameId!: string;
  s3Url: string = '';
  
  @ViewChild('gameCanvasA', { static: true }) gameCanvasA!: ElementRef<HTMLCanvasElement>;

  ngOnInit(): void {
    const gameId = localStorage.getItem("gameId");
    if (gameId)
      this.gameId = gameId;

    const canvas = this.gameCanvasA.nativeElement;
    if (canvas) {
      const context = canvas.getContext('2d');
      if (context) {
        // grab ROM from backend S3 storage
        firstValueFrom(this.svc.getGameROM(this.gameId))
          .then(resp => fetch(resp.romUrl))
          .then(file => file.arrayBuffer())
          .then(arrBuffer => {
            this.gameboy.loadGame(arrBuffer);
            // this.gameboy.apu.enableSound();
            this.gameboy.onFrameFinished((imageData: ImageData) => {
              context.putImageData(imageData, 0, 0);
            });
            this.gameboy.run();
          })
          .catch(err => {
            console.error("Error loading ROM", err);
          });
      } else {
        console.error("2D context is not available on the canvas.");
      }
    } else {
      console.error("Canvas element is not found.");
    }
  }
}
