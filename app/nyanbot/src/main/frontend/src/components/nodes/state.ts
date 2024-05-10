import {Connection, ConnectionId, Control, ControlId, isControlTextArea, Node, NodeId, NodeType, Position, Rect, Size, Translate} from "@/components/nodes/types.ts";

export type CanvasState = {
    scale: number;
    translate: Translate;
    position: Position;
    size: Size;
    rect: Rect;
    readonly: boolean;
}

export type EditorState = {
    connections: {
        [id: ConnectionId]: {
            id: ConnectionId;
        }
    };
    nodes: {
        [id: NodeId]: {
            id: NodeId;
            type: NodeType
        }
    };
    controls: {
        [id: ControlId]: {
            id: NodeId
        }
    }
    nodeControlIds: { [id: NodeId]: ControlId[] };
    canvas: CanvasState
}

export type EditorAction =
    | { type: "CANVAS_SET"; position: Position; size: Size; rect: Rect }
    | { type: "CONTROL_TEXT_AREA_UPDATED"; id: ControlId, value: string }
    | { type: "NODE_POSITION_UPDATED"; id: NodeId, position: Position }

export const editorReducer = (state: EditorState, action: EditorAction): EditorState => {
    switch (action.type) {
        case "CANVAS_SET":
            return {
                ...state, canvas: {
                    ...state.canvas,
                    position: action.position,
                    size: action.size,
                    rect: action.rect,
                }
            }
        case 'CONTROL_TEXT_AREA_UPDATED':
            // FIXME
            const control = state.controls[action.id]
            if (isControlTextArea(control)) {
                control.value = action.value;
            } else {
                throw Error('Not ControlText')
            }
            return {...state}
        case "NODE_POSITION_UPDATED":
            console.log("reduced")
            return state;
        default:
            return state;
    }
}

export const editorInitialState = (
    nodes: Node[],
    controls: Control[],
    connections: Connection[]
): EditorState => {
    return {
        connections: connections.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur}
        }, {}),
        nodes: nodes.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur}
        }, {}),
        controls: controls.reduce((acc, cur) => {
            return {...acc, [cur.id]: cur};
        }, {}),
        nodeControlIds: controls.reduce((acc, cur) => {
            acc[cur.nodeId] = acc[cur.nodeId] || []
            acc[cur.nodeId].push(cur.id)
            return acc
        }, {}),
        canvas: {
            scale: 1,
            translate: {x: 0, y: 0},
            readonly: false,
            position: {x: 0, y: 0},
            size: {width: 0, height: 0},
            rect: {left: 0, right: 0, top: 0, bottom: 0}
        }
    }
}



