import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class GameService {

    private http = inject(HttpClient);

    isGameStarted(): boolean {
        return (localStorage.getItem("gameStarted") == "true") ? true : false;
    }

    checkLogin() {

    }

    getAllGameDetails(): Observable<any> {
        return this.http.get<any>('/api/get-all-games')
            .pipe(result => {return result})
    }

    startLobby(numOfTeam: number, gameId: string) {
        // generate game host ID
        // let hostId = this.generateHostId()
        
        // use game host ID to generate QR code --> http call to springboot --> external API call (to protect API key)
        // QR code needs to launch telegram bot
        // let qrCode;
        // this.generateQrCode(hostId).subscribe(QR => {qrCode = QR})



        // route to lobby screen
1
        console.log('>>> selected:', numOfTeam, gameId)
    }

    startHostedGame(numOfTeam: number, gameId: string) {
        // generate chatboxes x numOfTeam

        // generate game emulator x numOfTeam

        // retrieve game ROM using gameId --> http call to springboot --> mySQL/S3

        // load all game emulators with game ROM
    }

    generateHostId(): string {
        return uuidv4().replaceAll('-','').substring(24,32);
    }

    generateTelegramLink(hostId: string): string {
        let queryString = `hostId=${hostId}`
        let base64QueryString = btoa(queryString)
        return `https://t.me/gamebond_bot?start=${base64QueryString}`;
    }

    generateQrCode(hostId: string) {
        let telegramUrl = this.generateTelegramLink(hostId);
        return this.http.post<any>('/api/get-QR', telegramUrl)
            .pipe(result => {return result})
    }

}