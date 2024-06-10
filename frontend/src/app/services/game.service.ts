import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class GameService {

    private http = inject(HttpClient);

    getAllGameDetails(): Observable<any> {
        return this.http.get<any>('/api/get-all-games')
            .pipe(result => {return result})
    }

    startHostedGame(numOfTeam: number, gameId: string) {
        // generate game host ID
        let hostId = this.generateHostId()
        
        // use game host ID to generate QR code --> http call to springboot --> external API call (to protect API key)
        // QR code needs to launch telegram bot
        this.generateQrCode(hostId)

        // generate chatboxes x numOfTeam

        // generate game emulator x numOfTeam

        // retrieve game ROM using gameId --> http call to springboot --> mySQL/S3

        // load all game emulators with game ROM

        console.log('>>> selected:', numOfTeam, gameId)
    }

    generateHostId(): string {
        return uuidv4().replaceAll('-','').substring(24,32);
    }

    generateQrCode(hostId: string) {
        let queryString = `hostId=${hostId}`
        let base64QueryString = btoa(queryString)
        let telegramUrl = `https://t.me/gamebond_bot?start=${base64QueryString}`
        return this.http.post<any>('/api/get-QR', telegramUrl)
            .pipe(result => {return result})
    }

}