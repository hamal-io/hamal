export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    groupId: string;
    defaultNamespaceIds: Map<string, string>;
    token: string;
    name: string;
}


export interface ApiLoginSubmitted {
    id: string;
    status: string;
    accountId: string;
    groupIds: string[];
    defaultNamespaceIds: Map<string, string>;
    token: string;
    name: string;
}
