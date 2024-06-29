export interface Membership {
    tier: number
    name: string
    monthlyGames: number
    maxRoms: number
    price: number
}

export const MEMBERSHIPS: Membership[] = [
    {
        tier: 0,
        name: 'Free',
        monthlyGames: 5,
        maxRoms: 5,
        price: 0
    },
    {
        tier: 1,
        name: 'Lite',
        monthlyGames: 20,
        maxRoms: 8,
        price: 2.99
    },
    {
        tier: 2,
        name: 'Standard',
        monthlyGames: 50,
        maxRoms: 10,
        price: 5.99
    },
    {
        tier: 3,
        name: 'Premium',
        monthlyGames: 100,
        maxRoms: 15,
        price: 7.99
    }
];