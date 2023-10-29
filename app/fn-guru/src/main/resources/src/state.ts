export type AuthStateType = 'Unauthorized' | 'Anonymous' | 'User'


export const AUTH_STATE_NAME = 'auth'

export interface AuthState {
    type: AuthStateType,
    accountId: string;
    token: string;
}
