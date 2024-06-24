import { Injectable, inject } from "@angular/core";
import { ComponentStore } from "@ngrx/component-store";
import { HostGame, HostGameState } from "../models/hostgame.model";
import { GameService } from "../services/game.service";

@Injectable()
export class GameStore extends ComponentStore<HostGameState> {

    constructor() {
        super({ gameState: {} as HostGame })
    }

    private readonly gameSvc = inject(GameService);

    // host new game with partial data (gameId and numOfTeams)
    readonly hostNewGame = this.updater<HostGame>(
        (currState: HostGameState, game: HostGame) => {
            const newGame: HostGame = {
                gameId: game.gameId,
                numOfTeams: game.numOfTeams,
                // hostId: this.gameSvc.generateHostId(game.numOfTeams)
                hostId: 'e0133494'
            } as HostGame
          return { gameState: newGame }
    })

    readonly getGame = this.select<HostGame>(
        (currState: HostGameState) => {
            return { ...currState.gameState };
    })

    readonly isValidGame = this.select<boolean>(
        (currState: HostGameState) => {
            const copy = { ...currState.gameState };
            if (copy.hostId.length == 8 && copy.gameId.length == 8 && copy.numOfTeams >= 1 && copy.numOfTeams <= 4)
                return true;
            return false;
    })

}