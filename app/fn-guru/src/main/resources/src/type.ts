export type AuthType = 'Anonymous' | 'User'

export interface Auth {
    type: AuthType,
    accountId: string;
    token: string;
}