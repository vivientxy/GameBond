export interface GameRom {
    gameId: string
    gameTitle: string
    romFile: string
    pictureUrl: string
}

export interface GameRomSlice {
    roms: GameRom[]
}