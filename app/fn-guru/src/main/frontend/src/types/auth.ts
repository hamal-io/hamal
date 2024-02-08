import {DefaultNamespaceId} from "@/types/namespace.ts";

export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    groupId: string;
    defaultNamespaceIds: Array<DefaultNamespaceId>;
    token: string;
}


export interface LoginSubmitted {
    id: string;
    status: string;
    accountId: string;
    groupIds: string[];
    defaultNamespaceIds: Array<DefaultNamespaceId>;
    token: string;
    email: string;
}
