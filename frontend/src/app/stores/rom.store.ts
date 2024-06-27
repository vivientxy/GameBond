import { Injectable } from "@angular/core";
import { ComponentStore } from "@ngrx/component-store";
import { GameRom, GameRomSlice } from "../models/gamerom.model";

@Injectable()
export class RomStore extends ComponentStore<GameRomSlice> {

    constructor() {
        super({ roms: [] })
    }

    readonly addRom = this.updater<GameRom>(
        (currState: GameRomSlice, newRom: GameRom) => {
            const newState: GameRomSlice = { ...currState };
            newState.roms.push(newRom);
            return newState;
        }
    )

    addRoms(newRoms: GameRom[]) {
        newRoms.forEach(rom => {this.addRom(rom)})
    }
    
    readonly getRoms = this.select<GameRom[]>(
        (currState: GameRomSlice) => {
            return currState.roms.map(rom => {
                return {
                    gameId: rom.gameId,
                    gameTitle: rom.gameTitle,
                    pictureUrl: rom.pictureUrl,
                    romFile: rom.romFile
                } as GameRom
            })
        }
    )

}