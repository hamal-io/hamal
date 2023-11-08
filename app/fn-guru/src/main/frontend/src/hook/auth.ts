import {useLocalStorage} from "@/hook/storage.ts";
import {Auth, AUTH_KEY} from "@/types/auth.ts";


export const useAuth = () => {
    return useLocalStorage<Auth>(AUTH_KEY, {
        type: 'Unauthorized',
        accountId: '',
        groupId: '',
        token: '',
        name: ''
    })
}