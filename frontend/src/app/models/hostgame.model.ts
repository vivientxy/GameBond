export interface HostGame {
    hostId: string
    gameId: string
    numOfTeams: number
    // qr: string
}

export interface HostGameState{
    gameState: HostGame;
}