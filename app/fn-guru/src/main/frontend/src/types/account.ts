export interface AccountConvertRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    token: string;
    email: string;
}

export interface AccountUpdateRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}