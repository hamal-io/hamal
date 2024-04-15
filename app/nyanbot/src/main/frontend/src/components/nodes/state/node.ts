import {Node, NodeId, Position} from "@/components/nodes/types.ts";

export type State = {
    nodes: { [id: NodeId]: Node };
}

export type Action =
    | { type: "NODE_POSITION_UPDATED"; id: NodeId, position: Position }


export const reducer = (state: State, action: Action): State => {
    switch (action.type) {
        default:
            return state;
    }
}

export const initialState = (nodes: Node[]): State => {
    return {
        nodes: nodes.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur}
        }, {})
    }
}