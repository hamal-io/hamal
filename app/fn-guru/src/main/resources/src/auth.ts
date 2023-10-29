import {Auth} from "./type.ts";
import global from "./global.ts";


export const clearAuth = () => {
    localStorage.removeItem('auth')
    global.auth = null
}

export const storeAuth = (auth: Auth) => {
    localStorage.setItem('auth', JSON.stringify({
        type: auth.type,
        accountId: auth.accountId,
        token: auth.token
    }))
}

export const loadAuth = () => {
    const auth = JSON.parse(localStorage.getItem('auth')) as Auth
    global.auth = auth;
    return auth
}

export const setAuth = (auth: Auth) => {
    global.auth = {
        type: auth.type,
        accountId: auth.accountId,
        token: auth.token
    }
}