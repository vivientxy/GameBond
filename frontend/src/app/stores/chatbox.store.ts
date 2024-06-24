import { Injectable } from "@angular/core";
import { ComponentStore } from "@ngrx/component-store";
import { Chat, ChatSlice } from "../models/chatbox.model";

@Injectable()
export class ChatboxStore extends ComponentStore<ChatSlice> {

    constructor() {
        super({ TeamA: [], TeamB: [], TeamC: [], TeamD: [] })
    }

    readonly addChatTeamA = this.updater<Chat>(
        (currState: ChatSlice, newChat: Chat) => {
            const newState: ChatSlice = { ...currState };
            newState.TeamA.push(newChat);
            return newState;
    })

    readonly addChatTeamB = this.updater<Chat>(
        (currState: ChatSlice, newChat: Chat) => {
            const newState: ChatSlice = { ...currState };
            newState.TeamB.push(newChat);
            return newState;
    })
    
    readonly addChatTeamC = this.updater<Chat>(
        (currState: ChatSlice, newChat: Chat) => {
            const newState: ChatSlice = { ...currState };
            newState.TeamC.push(newChat);
            return newState;
    })
    
    readonly addChatTeamD = this.updater<Chat>(
        (currState: ChatSlice, newChat: Chat) => {
            const newState: ChatSlice = { ...currState };
            newState.TeamD.push(newChat);
            return newState;
    })
    
    readonly getChatsTeamA = this.select<Chat[]>(
        (currState: ChatSlice) => {
            return currState.TeamA.map(chat => { 
                return {
                    username: chat.username,
                    message: chat.message
                } as Chat;
            }).reverse();
    })

    readonly getChatsTeamB = this.select<Chat[]>(
        (currState: ChatSlice) => {
            return currState.TeamB.map(chat => { 
                return {
                    username: chat.username,
                    message: chat.message
                } as Chat;
            }).reverse();
    })

    readonly getChatsTeamC = this.select<Chat[]>(
        (currState: ChatSlice) => {
            return currState.TeamC.map(chat => { 
                return {
                    username: chat.username,
                    message: chat.message
                } as Chat;
            }).reverse();
    })

    readonly getChatsTeamD = this.select<Chat[]>(
        (currState: ChatSlice) => {
            return currState.TeamD.map(chat => { 
                return {
                    username: chat.username,
                    message: chat.message
                } as Chat;
            }).reverse();
    })

}