import {Connection, ConnectionId} from "@/components/nodes/types.ts";

export type State = {
    connections: { [id: ConnectionId]: Connection };
}

export type Action =
    | { type: "CONNECTION_ADDED"; connection: Connection }


export const reducer = (state: State, action: Action): State => {
    switch (action.type) {
        default:
            return state;
    }
}

export const initialState = (connections: Connection[]): State => {
    return {
        connections: connections.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur}
        }, {})
    }
}