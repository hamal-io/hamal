export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    workspaceId: string;
    token: string;
}

export interface LoginRequested {
    id: string;
    status: string;
    accountId: string;
    workspaceIds: string[];
    token: string;
    email: string;
}
