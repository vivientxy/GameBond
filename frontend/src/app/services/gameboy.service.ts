import { ElementRef, inject, Injectable } from "@angular/core";
import { Gameboy } from "gameboy-emulator";
import { timer, tap } from "rxjs";
import { GameService } from "./game.service";
import { ChatboxStore } from "../stores/chatbox.store";
import { WebSocketService } from "./websocket.service";

@Injectable()
export class GameboyService {

    processInput(input: string, gameboy: Gameboy) {
        this.deactivateInputs(gameboy);
        switch (input) {
          case 'A':
            gameboy.input.isPressingA = true;
            break;
          case 'B':
            gameboy.input.isPressingB = true;
            break;
          case 'LEFT':
            gameboy.input.isPressingLeft = true;
            break;
          case 'RIGHT':
            gameboy.input.isPressingRight= true;
            break;
          case 'DOWN':
            gameboy.input.isPressingDown = true;
            break;
          case 'UP':
            gameboy.input.isPressingUp = true;
            break;
          case 'START':
            gameboy.input.isPressingStart = true;
            break;
          case 'SELECT':
            gameboy.input.isPressingSelect = true;
            break;
          default:
            break;
        }
    
        timer(100).pipe(tap(() => this.deactivateInputs(gameboy))).subscribe();
    }

    deactivateInputs(gameboy: Gameboy) {
        gameboy.input.isPressingA = false;
        gameboy.input.isPressingB = false;
        gameboy.input.isPressingLeft = false;
        gameboy.input.isPressingRight = false;
        gameboy.input.isPressingDown = false;
        gameboy.input.isPressingUp = false;
        gameboy.input.isPressingStart = false;
        gameboy.input.isPressingSelect = false;
    }
}