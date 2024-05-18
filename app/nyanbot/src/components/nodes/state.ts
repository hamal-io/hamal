import {
    Connection,
    ConnectionId,
    Control,
    ControlId, ControlInputBoolean, ControlInvoke,
    isControlInputString, isControlWithPort,
    Node,
    NodeId,
    NodeType,
    PortId,
    Position,
    Rect,
    Size,
    Translate
} from "@/components/nodes/types.ts";
import {filter} from "lodash";

export type CanvasState = {
    scale: number;
    translate: Translate;
    position: Position;
    size: Size;
    rect: Rect;
    readonly: boolean;
}

export type EditorState = {
    current: {
        id: string;
        type: "Node" | "Connection";
    };
    connections: {
        [id: ConnectionId]: {
            id: ConnectionId;
            outputPort: {
                id: PortId
            },
            inputPort: {
                id: PortId
            }
        }
    };
    nodes: {
        [id: NodeId]: {
            id: NodeId;
            type: NodeType,
            title: string;
            position: Position;
            size: Size;
            outputs: Array<{
                id: PortId
            }>
        }
    };
    ports: {
        [id: PortId]: {
            id: PortId
            nodeId: NodeId
        }
    }
    controls: {
        [id: ControlId]: Control
    }
    nodeControlIds: { [id: NodeId]: ControlId[] };
    canvas: CanvasState
}

export type EditorAction =
    | { type: "CANVAS_SET"; position: Position; size: Size; rect: Rect }
    | { type: "CONNECTION_ADDED"; outputPortId: PortId, inputPortId: PortId }
    | { type: "CONTROL_INPUT_STRING_UPDATED"; id: ControlId, value: string }
    | { type: "CONTROL_INPUT_BOOLEAN_UPDATED"; id: ControlId, value: boolean }
    | { type: "NODE_ADDED"; nodeType: NodeType; position: Position }
    | { type: "NODE_SELECTED"; id: NodeId; }
    | { type: "NODE_POSITION_UPDATED"; position: Position }
    | { type: "NODE_UNSELECTED"; }

export const editorReducer = (state: EditorState, action: EditorAction): EditorState => {
    const nextConnectionId = () => (Object.keys(state.connections).length + 1)
    const nextControlId = () => (Object.keys(state.controls).length + 1)
    const nextNodeId = () => (Object.keys(state.nodes).length + 1)
    const nextPortId = () => (Object.keys(state.ports).length + 1)

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
        case "CONNECTION_ADDED": {
            const connectionId = nextConnectionId()
            const copy = structuredClone(state)

            copy.connections[connectionId] = {
                id: connectionId.toString(),
                outputPort: {
                    id: action.outputPortId
                },
                outputNode: {
                    id: state.ports[action.outputPortId].nodeId
                },
                inputPort: {
                    id: action.inputPortId
                },
                inputNode: {
                    id: state.ports[action.inputPortId].nodeId
                }
            }
            return copy;
        }
        case 'CONTROL_INPUT_STRING_UPDATED': {
            // FIXME
            const control = state.controls[action.id]
            if (isControlInputString(control)) {
                control.value = action.value;
            } else {
                throw Error('Not ControlText')
            }
            return {...state}
        }

        case "CONTROL_INPUT_BOOLEAN_UPDATED": {
            const copy = structuredClone(state)

            copy.controls[action.id].value = action.value

            return copy
        }
        case "NODE_ADDED": {
            const copy = structuredClone(state)

            if (action.nodeType == 'Input') {

                const nodeId = nextNodeId().toString()
                const portId = nextPortId().toString()
                const controlId = nextControlId().toString()
                const controlPortId = (nextPortId() + 1).toString()

                copy.nodes[nodeId.toString()] = {
                    id: nodeId.toString(),
                    type: 'Input',
                    title: 'Input',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: [{
                        id: portId.toString(),
                        type: 'Boolean'
                    }]
                }

                copy.ports[portId.toString()] = {
                    id: portId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.ports[controlPortId.toString()] = {
                    id: controlPortId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Input_Boolean',
                    nodeId: nodeId.toString(),
                    value: false,
                    port: {
                        id: controlPortId,
                        type: "Boolean"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[nodeId] = [controlId.toString()]


            } else if (action.nodeType == 'Input_String') {

                const nodeId = nextNodeId().toString()
                const portId = nextPortId().toString()
                const controlId = nextControlId().toString()
                const controlPortId = (nextPortId() + 1).toString()

                copy.nodes[nodeId.toString()] = {
                    id: nodeId.toString(),
                    type: 'Input',
                    title: 'Input - String',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: [{
                        id: portId.toString(),
                        type: 'String'
                    }]
                }

                copy.ports[portId.toString()] = {
                    id: portId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.ports[controlPortId.toString()] = {
                    id: controlPortId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Input_String',
                    nodeId: nodeId.toString(),
                    value: 'Meeooowww',
                    port: {
                        id: controlPortId,
                        type: "String"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[nodeId] = [controlId.toString()]


            } else if (action.nodeType == 'Filter') {

                const nodeId = nextNodeId().toString()
                const portId = nextPortId().toString()
                const invokePortId = (nextPortId() + 1).toString()
                const controlId = nextControlId().toString()
                const filterControlId = (nextControlId() + 1).toString()
                const filterPortId = (nextPortId() + 1).toString()

                copy.nodes[nodeId.toString()] = {
                    id: nodeId.toString(),
                    type: 'Filter',
                    title: 'Filter',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: [{
                        id: portId.toString(),
                        type: 'Boolean'
                    }]
                }

                copy.ports[portId.toString()] = {
                    id: portId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.ports[invokePortId.toString()] = {
                    id: invokePortId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.ports[filterPortId.toString()] = {
                    id: filterPortId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Invoke',
                    nodeId: nodeId.toString(),
                    port: {
                        id: invokePortId.toString(),
                        type: "Boolean"
                    },
                    value: false
                } satisfies ControlInvoke

                copy.controls[filterControlId.toString()] = {
                    id: filterControlId.toString(),
                    type: "Input_Boolean",
                    nodeId: nodeId.toString(),
                    port: {
                        id: filterPortId,
                        type: "Boolean"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[nodeId] = [controlId.toString(), filterControlId.toString()]

            } else if (action.nodeType === 'Print') {

                const nodeId = nextNodeId().toString()
                const portId = nextPortId().toString()
                const controlId = nextControlId().toString()

                copy.nodes[nodeId.toString()] = {
                    id: nodeId.toString(),
                    type: 'Print',
                    title: 'Print',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: []
                }

                copy.ports[portId.toString()] = {
                    id: portId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Invoke',
                    nodeId: nodeId.toString(),
                    port: {
                        id: portId.toString(),
                        type: "Boolean"
                    },
                } satisfies ControlInvoke


                copy.nodeControlIds[nodeId] = [controlId.toString()]

            } else if (action.nodeType == "Telegram_Send_Message") {

                const nodeId = nextNodeId().toString()

                const invokePortId = nextPortId().toString()
                const invokeControlId = nextControlId().toString()

                copy.nodes[nodeId.toString()] = {
                    id: nodeId.toString(),
                    type: "Telegram_Send_Message",
                    title: 'Telegram - Send Message',
                    position: action.position,
                    size: {width: 250, height: 300},
                    outputs: []
                }

                copy.ports[invokePortId.toString()] = {
                    id: invokePortId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.controls[invokeControlId.toString()] = {
                    id: invokeControlId.toString(),
                    type: 'Invoke',
                    nodeId: nodeId.toString(),
                    port: {
                        id: invokePortId.toString(),
                        type: "Unit"
                    },
                } satisfies ControlInvoke


                const chatIdPortId = (nextPortId() + 1).toString()
                const chatIdControlId = (nextControlId() + 1).toString()


                copy.ports[chatIdPortId.toString()] = {
                    id: chatIdPortId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.controls[chatIdControlId.toString()] = {
                    id: chatIdControlId.toString(),
                    key: 'chat_id',
                    type: 'Input_String',
                    nodeId: nodeId.toString(),
                    port: {
                        id: chatIdPortId.toString(),
                        type: 'String'
                    },
                    value: '',
                    placeholder: 'chat_id'
                } satisfies ControlInvoke


                const messagePortId = (nextPortId() + 2).toString()
                const messageControlId = (nextControlId() + 2).toString()

                copy.ports[messagePortId] = {
                    id: messagePortId.toString(),
                    nodeId: nodeId.toString()
                }

                copy.controls[messageControlId.toString()] = {
                    id: messageControlId.toString(),
                    key: 'message',
                    type: 'Input_String',
                    nodeId: nodeId.toString(),
                    port: {
                        id: messagePortId.toString(),
                        type: 'String'
                    },
                    value: '',
                    placeholder: 'message'
                } satisfies ControlInvoke

                copy.nodeControlIds[nodeId] = [invokeControlId.toString(), chatIdControlId.toString(), messageControlId.toString()]

            } else {
                throw Error(`${action.nodeType} is not supported yet`)
            }

            return copy;
        }
        case "NODE_SELECTED": {
            const copy = structuredClone(state)
            copy.current = {
                id: action.id,
                type: "Node"
            };
            return copy;
        }
        case "NODE_UNSELECTED": {
            const copy = structuredClone(state)
            copy.current = null;
            return copy;
        }
        case "NODE_POSITION_UPDATED": {
            const copy = structuredClone(state)
            copy.nodes[state.current.id].position = action.position;
            return copy;
        }
        default:
            return state;
    }
}

export const editorInitialState = (
    nodes: Node[],
    controls: Control[],
    connections: Connection[]
): EditorState => {

    const ports = nodes.flatMap(node => node.outputs.map((port) => port)).concat(
        controls.map((control) => {
            if (isControlWithPort(control)) {
                return control.port
            } else {
                return null;
            }
        })
    )

    return {
        current: null,
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
        ports: ports.reduce((acc, cur) => {
            if (!cur) return acc;
            return {...acc, [cur.id]: cur}
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



