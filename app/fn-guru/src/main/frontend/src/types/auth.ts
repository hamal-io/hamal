export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    groupId: string;
    token: string;
    name: string;
}


export interface ApiLoginSubmitted {
    id: string;
    status: string;
    accountId: string;
    groupIds: string[];
    token: string;
    name: string;
}
