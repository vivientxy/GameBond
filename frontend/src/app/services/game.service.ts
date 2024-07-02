import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, map } from "rxjs";
import { v4 as uuidv4 } from 'uuid';
import { HostGame } from "../models/hostgame.model";

@Injectable()
export class GameService {

    private http = inject(HttpClient);

    getAllGameDetails(username: string): Observable<any> {
        const queryParams = new HttpParams()
            .set('username', username)
        return this.http.get<any>('/api/get-all-games', {params: queryParams})
            .pipe(result => {return result})
    }

    startLobby(numOfTeams: number, gameId: string): string {
        const hostId = this.generateHostId(numOfTeams);
        const game: HostGame = {hostId: hostId, gameId: gameId, numOfTeams: numOfTeams};
        sessionStorage.setItem("game", JSON.stringify(game));
        return hostId;
    }

    isValidGame(): boolean {
        let game = this.getGame();
        if (!game)
            return false;
        if (game.hostId.length == 8 && game.gameId.length == 8 && game.numOfTeams >= 1 && game.numOfTeams <= 4)
            return true;
        return false;
    }

    getGame(): HostGame | null {
        let gameString = sessionStorage.getItem('game');
        if (gameString)
            return JSON.parse(gameString) as HostGame;
        return null;
    }

    generateHostId(numOfTeams: number): string {
        // inject numOfTeams as part of hostId so players can manually key in hostId to join game
        let uuid : string = uuidv4().replaceAll('-','').substring(25,32);
        uuid += numOfTeams
        return uuid
    }

    generateTelegramLink(hostId: string): string {
        let queryString = `hostId=${hostId}`
        let base64QueryString = btoa(queryString)
        return `https://t.me/gamebond_bot?start=${base64QueryString}`;
    }

    generateQrCode(hostId: string) {
        let telegramUrl = this.generateTelegramLink(hostId);
        return this.http.post('/api/get-QR', telegramUrl)
            .pipe(result => {return result})
    }

    getGameROM(gameId: string) {
        return this.http.post<any>('/api/get-rom', gameId)
            .pipe(result => {return result})
    }

    addGameROM(form: FormData) {
        return this.http.post<any>('/api/add-rom', form)
            .pipe(result => {return result})
    }

    /* GAME ENDED */

    endGame(hostId: string) {
        const queryParams = new HttpParams()
            .set('hostId', hostId)
        return this.http.get<any>('/api/end-game', {params: queryParams})
    }

}