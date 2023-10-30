export const unauthorizedDefaultHeaders = () => ({
    'Accept': 'application/json',
    'Content-Type': 'application/json',
})

export const authorizedDefaultHeaders = () => {
    // const [auth] = useLocalStorageState<AuthState>(AUTH_STATE_NAME, undefined)

    return ({
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        // 'Authorization': `Bearer ${auth.token}`
    })
}
