import global from "../global.ts";

export const unauthorizedDefaultHeaders = () => ({
    'Accept': 'application/json',
    'Content-Type': 'application/json',
})


export const authorizedDefaultHeaders = () => ({
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${global.auth.token}`
})
