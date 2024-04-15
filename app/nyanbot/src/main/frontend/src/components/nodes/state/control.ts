import {Control, ControlId, isControlText, NodeId} from "@/components/nodes/types.ts";

export type State = {
    controls: { [id: ControlId]: Control };
    nodeControlIds: { [id: NodeId]: ControlId[] }
}

export type Action =
    | { type: "CONTROL_TEXT_UPDATED"; id: ControlId, value: string }


export const reducer = (state: State, action: Action): State => {
    switch (action.type) {
        case 'CONTROL_TEXT_UPDATED':
            // FIXME
            const control = state.controls[action.id]
            if (isControlText(control)) {
                control.defaultValue = action.value;
            } else {
                throw Error('Not ControlText')
            }
            return {...state}
        default:
            return state;
    }
}

export const initialState = (controls: Control[]): State => {
    return {
        controls: controls.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur};
        }, {}),
        nodeControlIds: controls.reduce((acc, cur) => {
            acc[cur.nodeId] = acc[cur.nodeId] || []
            acc[cur.nodeId].push(cur.id)
            return acc
        }, {}),
    }
}