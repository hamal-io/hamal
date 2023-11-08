import {useLocalStorage} from "@/hook/storage.ts";

export type AuthType = 'Unauthorized' | 'Anonymous' | 'User'

export interface Auth {
    type: AuthType,
    accountId: string;
    groupId: string;
    token: string;
    name: string;
}

const AUTH_KEY = 'auth'

export const useAuth = () => {
    return useLocalStorage<Auth>(AUTH_KEY, {
        type: 'Unauthorized',
        accountId: '',
        groupId: '',
        token: '',
        name: ''
    })
}