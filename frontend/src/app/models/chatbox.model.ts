export interface Chat {
    username: string
    message: string
}

export interface ChatSlice {
    TeamA: Chat[]
    TeamB: Chat[]
    TeamC: Chat[]
    TeamD: Chat[]
}