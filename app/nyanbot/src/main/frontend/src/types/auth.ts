export type AuthType = 'Unauthorized' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    workspaceId: string;
    token: string;
}

export interface LoginRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    workspaceIds: string[];
    token: string;
    email: string;
}
