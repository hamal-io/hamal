import {DefaultFlowId} from "@/types/flow.ts";

export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export const AUTH_KEY = 'auth'

export interface Auth {
    type: AuthType,
    accountId: string;
    groupId: string;
    defaultFlowIds: Array<DefaultFlowId>;
    token: string;
}


export interface LoginSubmitted {
    id: string;
    status: string;
    accountId: string;
    groupIds: string[];
    defaultFlowIds: Array<DefaultFlowId>;
    token: string;
    email: string;
}
