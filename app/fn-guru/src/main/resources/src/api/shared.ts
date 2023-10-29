import global from "../global.ts";
import {loadAuth} from "../auth.ts";

export const unauthorizedDefaultHeaders = () => ({
    'Accept': 'application/json',
    'Content-Type': 'application/json',
})


export const authorizedDefaultHeaders = () => {
    const auth = global.auth || loadAuth()
    return ({
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    })
}
