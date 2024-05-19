import {
    Connection,
    ConnectionIndex,
    Control,
    ControlIndex, ControlInputBoolean, ControlInputString,
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
        index: NodeIndex | ControlIndex | PortIndex;
        type: "Node" | "Connection";
    };
    connections: {
        [index: ConnectionIndex]: {
            index: ConnectionIndex;
            outputPort: {
                index: PortIndex;
            };
            outputNode: {
                index: NodeIndex;
            };
            inputPort: {
                index: PortIndex;
            };
            inputNode: {
                index: NodeIndex;
            }
        }
    };
    nodes: {
        [index: NodeIndex]: {
            index: NodeIndex;
            type: NodeType,
            title: string;
            position: Position;
            size: Size;
            inputs: Array<{
                index: PortIndex;
                form: string;
            }>;
            outputs: Array<{
                index: PortIndex;
                form: string;
            }>
        }
    };
    ports: {
        [index: PortIndex]: {
            index: PortIndex
            nodeIndex: NodeIndex
        }
    }
    controls: {
        [index: ControlIndex]: Control
    }
    nodeControlIds: { [index: NodeIndex]: ControlIndex[] };
    canvas: CanvasState
}

export type EditorAction =
    | { type: "CANVAS_SET"; position: Position; size: Size; rect: Rect }
    | { type: "CONNECTION_ADDED"; outputPortIndex: PortIndex, inputPortIndex: PortIndex }
    | { type: "CONTROL_INPUT_BOOLEAN_UPDATED"; index: ControlIndex, value: boolean }
    | { type: "CONTROL_INPUT_STRING_UPDATED"; index: ControlIndex, value: string }
    | { type: "NODE_ADDED"; nodeType: NodeType; position: Position }
    | { type: "NODE_SELECTED"; index: NodeIndex; }
    | { type: "NODE_POSITION_UPDATED"; position: Position }
    | { type: "NODE_UNSELECTED"; }

export const editorReducer = (state: EditorState, action: EditorAction): EditorState => {
    const nextConnectionIndex = () => (Object.keys(state.connections).length + 1)
    const nextControlIndex = () => (Object.keys(state.controls).length + 1)
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
                index: ConnectionIndex,
                outputPort: {
                    index: action.outputPortIndex
                },
                outputNode: {
                    index: state.ports[action.outputPortIndex].nodeIndex
                },
                inputPort: {
                    index: action.inputPortIndex
                },
                inputNode: {
                    index: state.ports[action.inputPortIndex].nodeIndex
                }
            }
            return copy;
        }
        case 'CONTROL_INPUT_STRING_UPDATED': {
            // FIXME
            const control = state.controls[action.index]
            if (isControlInputString(control)) {
                control.value = action.value;
            } else {
                throw Error('Not ControlText')
            }
            return {...state}
        }

        case "CONTROL_INPUT_BOOLEAN_UPDATED": {
            const copy = structuredClone(state)

            copy.controls[action.index].value = action.value

            return copy
        }
        case "NODE_ADDED": {
            const copy = structuredClone(state)

            if (action.nodeType == 'Input') {

                const nodeIndex = nextNodeIndex()
                const portIndex = nextPortIndex()
                const controlId = nextControlIndex()
                const controlPortIndex = (nextPortIndex() + 1)

                copy.nodes[nodeIndex] = {
                    index: nodeIndex,
                    type: 'Input',
                    title: 'Input',
                    position: action.position,
                    size: {width: 100, height: 100},
                    inputs: [],
                    outputs: [{
                        index: portIndex,
                        form: 'Boolean'
                    }]
                }

                copy.ports[portIndex] = {
                    index: portIndex,
                    nodeIndex: nodeIndex
                }

                copy.ports[controlPortIndex] = {
                    index: controlPortIndex,
                    nodeIndex: nodeIndex
                }

                copy.controls[controlId] = {
                    index: controlId,
                    type: 'Input_Boolean',
                    nodeIndex: nodeIndex,
                    value: false,
                    port: {
                        index: controlPortIndex,
                        form: "Boolean"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[nodeIndex] = [controlId]


            } else if (action.nodeType == 'Input_String') {

                const nodeIndex = nextNodeIndex()
                const portIndex = nextPortIndex()
                const controlId = nextControlIndex()
                const controlPortIndex = (nextPortIndex() + 1)

                copy.nodes[nodeIndex] = {
                    index: nodeIndex,
                    type: 'Input',
                    title: 'Input - String',
                    position: action.position,
                    size: {width: 100, height: 100},
                    inputs: [],
                    outputs: [{
                        index: portIndex,
                        form: 'String'
                    }]
                }

                copy.ports[portIndex] = {
                    index: portIndex,
                    nodeIndex: nodeIndex
                }

                copy.ports[controlPortIndex] = {
                    index: controlPortIndex,
                    nodeIndex: nodeIndex
                }

                copy.controls[controlId] = {
                    index: controlId,
                    type: 'Input_String',
                    nodeIndex: nodeIndex,
                    value: 'Meeooowww',
                    port: {
                        index: controlPortIndex,
                        form: "String"
                    }
                } satisfies ControlInputString

                copy.nodeControlIds[nodeIndex] = [controlId]


            } else if (action.nodeType == 'Filter') {

                const nodeIndex = nextNodeIndex()
                const outputPortIndex = nextPortIndex()
                const inputPortIndex = (nextPortIndex() + 1)
                // const controlId = nextControlIndex()
                const filterControlId = (nextControlIndex() )
                const filterPortIndex = (nextPortIndex() + 2)

                copy.nodes[nodeIndex] = {
                    index: nodeIndex,
                    type: 'Filter',
                    title: 'Filter',
                    position: action.position,
                    size: {width: 100, height: 100},
                    inputs: [{
                        index: inputPortIndex,
                        form: 'Boolean'
                    }],
                    outputs: [{
                        index: outputPortIndex,
                        form: 'Boolean'
                    }]
                }

                copy.ports[outputPortIndex] = {
                    index: outputPortIndex,
                    nodeIndex: nodeIndex
                }

                copy.ports[inputPortIndex] = {
                    index: inputPortIndex,
                    nodeIndex: nodeIndex
                }

                copy.ports[filterPortIndex] = {
                    index: filterPortIndex,
                    nodeIndex: nodeIndex
                }

                // copy.controls[controlId] = {
                //     index: controlId,
                //     type: 'Invoke',
                //     nodeIndex: nodeIndex,
                //     port: {
                //         id: invokePortIndex,
                //         type: "Boolean"
                //     },
                //     value: false
                // } satisfies ControlInvoke

                copy.controls[filterControlId] = {
                    index: filterControlId,
                    type: "Input_Boolean",
                    nodeIndex: nodeIndex,
                    port: {
                        index: filterPortIndex,
                        form: "Boolean"
                    }
                } satisfies ControlInputBoolean

                copy.nodeControlIds[nodeIndex] = [ filterControlId]

            } else if (action.nodeType === 'Print') {

                const nodeIndex = nextNodeIndex()
                const portIndex = nextPortIndex()
                // const controlId = nextControlIndex()

                copy.nodes[nodeIndex] = {
                    index: nodeIndex,
                    type: 'Print',
                    title: 'Print',
                    position: action.position,
                    size: {width: 100, height: 100},
                    inputs: [
                        {
                            index: portIndex,
                            form: "Boolean"
                        },
                    ],
                    outputs: []
                }

                copy.ports[portIndex] = {
                    index: portIndex,
                    nodeIndex: nodeIndex
                }

                // copy.controls[controlId] = {
                //     index: controlId,
                //     type: 'Invoke',
                //     nodeIndex: nodeIndex,
                //     port: {
                //         index: portIndex,
                //         type: "Boolean"
                //     },
                // } satisfies ControlInvoke


                copy.nodeControlIds[nodeIndex] = []

            } else if (action.nodeType == "Telegram_Send_Message") {

                const nodeIndex = nextNodeIndex()

                const invokePortIndex = nextPortIndex()

                copy.nodes[nodeIndex] = {
                    index: nodeIndex,
                    type: "Telegram_Send_Message",
                    title: 'Telegram - Send Message',
                    position: action.position,
                    size: {width: 250, height: 300},
                    inputs: [
                        {
                            index: invokePortIndex,
                            form: "Any"
                        },
                    ],
                    outputs: []
                }

                copy.ports[invokePortIndex] = {
                    index: invokePortIndex,
                    nodeIndex: nodeIndex
                }

                // copy.controls[invokeControlId] = {
                //     index: invokeControlId,
                //     type: 'Invoke',
                //     nodeIndex: nodeIndex,
                //     port: {
                //         index: invokePortIndex,
                //         type: "Unit"
                //     },
                // } satisfies ControlInvoke


                const chatIdPortIndex = (nextPortIndex())
                const chatIdControlId = (nextControlIndex())


                copy.ports[chatIdPortIndex] = {
                    index: chatIdPortIndex,
                    nodeIndex: nodeIndex
                }

                copy.controls[chatIdControlId] = {
                    index: chatIdControlId,
                    // key: 'chat_id',
                    type: 'Input_String',
                    nodeIndex: nodeIndex,
                    port: {
                        index: chatIdPortIndex,
                        form: 'String'
                    },
                    value: '',
                    placeholder: 'chat_id'
                } satisfies ControlInputString


                const messagePortIndex = (nextPortIndex() + 1)
                const messageControlId = (nextControlIndex() + 1)

                copy.ports[messagePortIndex] = {
                    index: messagePortIndex,
                    nodeIndex: nodeIndex
                }

                copy.controls[messageControlId] = {
                    index: messageControlId,
                    // key: 'message',
                    type: 'Input_String',
                    nodeIndex: nodeIndex,
                    port: {
                        index: messagePortIndex,
                        form: 'String'
                    },
                    value: '',
                    placeholder: 'message'
                } satisfies ControlInputString

                copy.nodeControlIds[nodeIndex] = [chatIdControlId, messageControlId]

            } else {
                throw Error(`${action.nodeType} is not supported yet`)
            }

            return copy;
        }
        case "NODE_SELECTED": {
            const copy = structuredClone(state)
            copy.current = {
                index: action.index,
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
            copy.nodes[state.current.index].position = action.position;
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
            return {...acc, [cur.index]: cur}
        }, {}),
        nodes: nodes.reduce((acc, cur) => {
            return {...acc, [cur.index]: cur}
        }, {}),
        controls: controls.reduce((acc, cur) => {
            return {...acc, [cur.index]: cur};
        }, {}),
        nodeControlIds: controls.reduce((acc, cur) => {
            acc[cur.nodeIndex] = acc[cur.nodeIndex] || []
            acc[cur.nodeIndex].push(cur.index)
            return acc
        }, {}),
        ports: ports.reduce((acc, cur) => {
            if (!cur) return acc;
            return {...acc, [cur.index]: cur}
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



