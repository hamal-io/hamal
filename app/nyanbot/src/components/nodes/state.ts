import {
    Connection,
    ConnectionIndex,
    Control,
    ControlId, ControlInputBoolean, ControlInvoke,
    isControlInputString, isControlWithPort,
    Node,
    NodeIndex,
    NodeType,
    PortIndex,
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
        [id: ConnectionIndex]: {
            id: ConnectionIndex;
            outputPort: {
                id: PortIndex
            },
            inputPort: {
                id: PortIndex
            }
        }
    };
    nodes: {
        [id: NodeIndex]: {
            id: NodeIndex;
            type: NodeType,
            title: string;
            position: Position;
            size: Size;
            outputs: Array<{
                id: PortIndex
            }>
        }
    };
    ports: {
        [id: PortIndex]: {
            id: PortIndex
            NodeIndex: NodeIndex
        }
    }
    controls: {
        [id: ControlId]: Control
    }
    nodeControlIds: { [id: NodeIndex]: ControlId[] };
    canvas: CanvasState
}

export type EditorAction =
    | { type: "CANVAS_SET"; position: Position; size: Size; rect: Rect }
    | { type: "CONNECTION_ADDED"; outputPortIndex: PortIndex, inputPortIndex: PortIndex }
    | { type: "CONTROL_INPUT_STRING_UPDATED"; id: ControlId, value: string }
    | { type: "CONTROL_INPUT_BOOLEAN_UPDATED"; id: ControlId, value: boolean }
    | { type: "NODE_ADDED"; nodeType: NodeType; position: Position }
    | { type: "NODE_SELECTED"; id: NodeIndex; }
    | { type: "NODE_POSITION_UPDATED"; position: Position }
    | { type: "NODE_UNSELECTED"; }

export const editorReducer = (state: EditorState, action: EditorAction): EditorState => {
    const nextConnectionIndex = () => (Object.keys(state.connections).length + 1)
    const nextControlId = () => (Object.keys(state.controls).length + 1)
    const nextNodeIndex = () => (Object.keys(state.nodes).length + 1)
    const nextPortIndex = () => (Object.keys(state.ports).length + 1)

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
            const ConnectionIndex = nextConnectionIndex()
            const copy = structuredClone(state)

            copy.connections[ConnectionIndex] = {
                id: ConnectionIndex.toString(),
                outputPort: {
                    id: action.outputPortIndex
                },
                outputNode: {
                    id: state.ports[action.outputPortIndex].NodeIndex
                },
                inputPort: {
                    id: action.inputPortIndex
                },
                inputNode: {
                    id: state.ports[action.inputPortIndex].NodeIndex
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

                const NodeIndex = nextNodeIndex().toString()
                const PortIndex = nextPortIndex().toString()
                const controlId = nextControlId().toString()
                const controlPortIndex = (nextPortIndex() + 1).toString()

                copy.nodes[NodeIndex.toString()] = {
                    id: NodeIndex.toString(),
                    type: 'Input',
                    title: 'Input',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: [{
                        id: PortIndex.toString(),
                        type: 'Boolean'
                    }]
                }

                copy.ports[PortIndex.toString()] = {
                    id: PortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.ports[controlPortIndex.toString()] = {
                    id: controlPortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Input_Boolean',
                    NodeIndex: NodeIndex.toString(),
                    value: false,
                    port: {
                        id: controlPortIndex,
                        type: "Boolean"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[NodeIndex] = [controlId.toString()]


            } else if (action.nodeType == 'Input_String') {

                const NodeIndex = nextNodeIndex().toString()
                const PortIndex = nextPortIndex().toString()
                const controlId = nextControlId().toString()
                const controlPortIndex = (nextPortIndex() + 1).toString()

                copy.nodes[NodeIndex.toString()] = {
                    id: NodeIndex.toString(),
                    type: 'Input',
                    title: 'Input - String',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: [{
                        id: PortIndex.toString(),
                        type: 'String'
                    }]
                }

                copy.ports[PortIndex.toString()] = {
                    id: PortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.ports[controlPortIndex.toString()] = {
                    id: controlPortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Input_String',
                    NodeIndex: NodeIndex.toString(),
                    value: 'Meeooowww',
                    port: {
                        id: controlPortIndex,
                        type: "String"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[NodeIndex] = [controlId.toString()]


            } else if (action.nodeType == 'Filter') {

                const NodeIndex = nextNodeIndex().toString()
                const PortIndex = nextPortIndex().toString()
                const invokePortIndex = (nextPortIndex() + 1).toString()
                const controlId = nextControlId().toString()
                const filterControlId = (nextControlId() + 1).toString()
                const filterPortIndex = (nextPortIndex() + 1).toString()

                copy.nodes[NodeIndex.toString()] = {
                    id: NodeIndex.toString(),
                    type: 'Filter',
                    title: 'Filter',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: [{
                        id: PortIndex.toString(),
                        type: 'Boolean'
                    }]
                }

                copy.ports[PortIndex.toString()] = {
                    id: PortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.ports[invokePortIndex.toString()] = {
                    id: invokePortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.ports[filterPortIndex.toString()] = {
                    id: filterPortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Invoke',
                    NodeIndex: NodeIndex.toString(),
                    port: {
                        id: invokePortIndex.toString(),
                        type: "Boolean"
                    },
                    value: false
                } satisfies ControlInvoke

                copy.controls[filterControlId.toString()] = {
                    id: filterControlId.toString(),
                    type: "Input_Boolean",
                    NodeIndex: NodeIndex.toString(),
                    port: {
                        id: filterPortIndex,
                        type: "Boolean"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[NodeIndex] = [controlId.toString(), filterControlId.toString()]

            } else if (action.nodeType === 'Print') {

                const NodeIndex = nextNodeIndex().toString()
                const PortIndex = nextPortIndex().toString()
                const controlId = nextControlId().toString()

                copy.nodes[NodeIndex.toString()] = {
                    id: NodeIndex.toString(),
                    type: 'Print',
                    title: 'Print',
                    position: action.position,
                    size: {width: 100, height: 100},
                    outputs: []
                }

                copy.ports[PortIndex.toString()] = {
                    id: PortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.controls[controlId.toString()] = {
                    id: controlId.toString(),
                    type: 'Invoke',
                    NodeIndex: NodeIndex.toString(),
                    port: {
                        id: PortIndex.toString(),
                        type: "Boolean"
                    },
                } satisfies ControlInvoke


                copy.nodeControlIds[NodeIndex] = [controlId.toString()]

            } else if (action.nodeType == "Telegram_Send_Message") {

                const NodeIndex = nextNodeIndex().toString()

                const invokePortIndex = nextPortIndex().toString()
                const invokeControlId = nextControlId().toString()

                copy.nodes[NodeIndex.toString()] = {
                    id: NodeIndex.toString(),
                    type: "Telegram_Send_Message",
                    title: 'Telegram - Send Message',
                    position: action.position,
                    size: {width: 250, height: 300},
                    outputs: []
                }

                copy.ports[invokePortIndex.toString()] = {
                    id: invokePortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.controls[invokeControlId.toString()] = {
                    id: invokeControlId.toString(),
                    type: 'Invoke',
                    NodeIndex: NodeIndex.toString(),
                    port: {
                        id: invokePortIndex.toString(),
                        type: "Unit"
                    },
                } satisfies ControlInvoke


                const chatIdPortIndex = (nextPortIndex() + 1).toString()
                const chatIdControlId = (nextControlId() + 1).toString()


                copy.ports[chatIdPortIndex.toString()] = {
                    id: chatIdPortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.controls[chatIdControlId.toString()] = {
                    id: chatIdControlId.toString(),
                    key: 'chat_id',
                    type: 'Input_String',
                    NodeIndex: NodeIndex.toString(),
                    port: {
                        id: chatIdPortIndex.toString(),
                        type: 'String'
                    },
                    value: '',
                    placeholder: 'chat_id'
                } satisfies ControlInvoke


                const messagePortIndex = (nextPortIndex() + 2).toString()
                const messageControlId = (nextControlId() + 2).toString()

                copy.ports[messagePortIndex] = {
                    id: messagePortIndex.toString(),
                    NodeIndex: NodeIndex.toString()
                }

                copy.controls[messageControlId.toString()] = {
                    id: messageControlId.toString(),
                    key: 'message',
                    type: 'Input_String',
                    NodeIndex: NodeIndex.toString(),
                    port: {
                        id: messagePortIndex.toString(),
                        type: 'String'
                    },
                    value: '',
                    placeholder: 'message'
                } satisfies ControlInvoke

                copy.nodeControlIds[NodeIndex] = [invokeControlId.toString(), chatIdControlId.toString(), messageControlId.toString()]

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
            acc[cur.NodeIndex] = acc[cur.NodeIndex] || []
            acc[cur.NodeIndex].push(cur.id)
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



