import { Injectable } from "@angular/core";
import { ComponentStore } from "@ngrx/component-store";
import { Chat, ChatSlice } from "../models/chatbox.model";
import { Observable } from "rxjs";

@Injectable()
export class ChatboxStore extends ComponentStore<ChatSlice> {

    constructor() {
        super({ TeamA: [], TeamB: [], TeamC: [], TeamD: [] })
    }

    readonly addChat = this.updater<{ team: keyof ChatSlice, chat: Chat }>(
        (currState: ChatSlice, { team, chat }) => {
            const newState = { ...currState };
            newState[team].push(chat);
            if (newState[team].length > 30)
                newState[team].splice(0,1);
            return newState;
        }
    )
    
    readonly getChats = (team: keyof ChatSlice): Observable<Chat[]> =>
        this.select(state => state[team].map(chat => ({
            username: chat.username,
            message: chat.message
        })).reverse()
    )

    readonly resetChats = this.updater<boolean>(
        (currState: ChatSlice, reset: boolean) => {
            return { TeamA: [], TeamB: [], TeamC: [], TeamD: [] };
        }
    )

}