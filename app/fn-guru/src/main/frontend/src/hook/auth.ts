import {Auth, AUTH_KEY} from "@/types/auth.ts";
import useLocalStorageState from "use-local-storage-state";


export const useAuth = () => {
    return useLocalStorageState<Auth>(AUTH_KEY, {
        defaultValue: {
            type: 'Unauthorized',
            accountId: '',
            groupId: '',
            defaultFlowIds: new Map<string, string>(),
            token: '',
            name: ''
        }
    })
}