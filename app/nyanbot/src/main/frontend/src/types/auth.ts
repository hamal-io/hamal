export type AuthType = 'Unauthorized' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    token: string;
}
