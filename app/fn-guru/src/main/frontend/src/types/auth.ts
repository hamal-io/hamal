export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    groupId: string;
    token: string;
}

export interface LoginRequested {
    id: string;
    status: string;
    accountId: string;
    groupIds: string[];
    token: string;
    email: string;
}
