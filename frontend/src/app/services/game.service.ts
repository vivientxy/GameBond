import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class GameService {

    private http = inject(HttpClient);

    getAllGameDetails(): Observable<any> {
        // retrieve all game infos using gameId --> http call to springboot --> mongodb

        return this.http.get<any>('/api/register')
            .pipe(result => {return result})
    }

    startHostedGame(numOfTeam: number, gameId: string) {
        // generate game host ID

        // use game host ID to generate QR code -- external API call

        // generate chatboxes x numOfTeam

        // generate game emulator x numOfTeam

        // retrieve game ROM using gameId --> http call to springboot --> mongodb/S3

        // load all game emulators with game ROM

      }

}