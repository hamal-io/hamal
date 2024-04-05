export interface AccountConvertRequested {
    requestId: string;
    requestStatus: string;
    id: string;
    token: string;
    email: string;
}

export interface AccountPasswordChangeRequested {
    requestId: string;
    requestStatus: string;
    id: string;
}