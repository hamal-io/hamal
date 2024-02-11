import {DefaultNamespaceId} from "@/types/namespace.ts";

export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    groupId: string;
    token: string;
}


export interface LoginSubmitted {
    id: string;
    status: string;
    accountId: string;
    groupIds: string[];
    token: string;
    email: string;
}
