import React, {FC} from 'react'
import {Canvas} from "@/components/nodes/canvas.tsx";
import ButtonPrimary from "@/components/old-ui/button/ButtonPrimary.tsx";
import {Connection, ControlCondition, ControlInput, ControlText, Graph, Node} from '@/components/nodes/types';

type EditorProps = {
    onSave: (graph: Graph) => void;
}

export const Editor: FC<EditorProps> = ({onSave}) => {
    const nodes: Node[] = [
        {
            id: '1',
            type: "Init",
            title: "Init",
            position: {x: -500, y: 0},
            size: {width: 200, height: 300},
            controls: [],
            outputs: [{
                id: '1'
            }]
        } satisfies Node,
        {
            id: '2',
            type: "Select",
            title: 'Select LP',
            position: {x: -150, y: 0},
            size: {width: 250, height: 300},
            controls: [
                {
                    id: '1',
                    type: 'Input',
                    ports: [{
                        id: '2'
                    }],
                } satisfies ControlInput,
                {
                    id: '2',
                    type: 'Condition',
                    ports: [],
                } satisfies ControlCondition,
            ],
            outputs: [{
                id: '3'
            }]
        } satisfies Node,

        {
            id: '3',
            type: "ToText",
            title: 'LP to text',
            position: {x: 200, y: 0},
            size: {width: 250, height: 300},
            controls: [
                {
                    id: '1',
                    type: 'Input',
                    ports: [{
                        id: '4'
                    }],
                } satisfies ControlInput,
                {
                    id: '2',
                    type: 'Text',
                    ports: [],
                    text: `{contract.address} has {total_holder}`,
                    placeholder: 'Turn into text'
                } satisfies ControlText,
            ],
            outputs: [{
                id: '5'
            }]
        } satisfies Node,

        {
            id: '4',
            type: "TelegramMessageSend",
            title: 'Telegram send message',
            position: {x: 550, y: 0},
            size: {width: 250, height: 300},
            controls: [
                {
                    id: '3',
                    type: 'Text',
                    ports: [],
                    text: '',
                    placeholder: 'chat_id'
                } satisfies ControlText,

                {
                    id: '4',
                    type: 'Text',
                    ports: [
                        {
                            id: '6'
                        }
                    ],
                    text: '',
                    placeholder: 'message'
                } satisfies ControlText
            ],
            outputs: []
        } satisfies Node

    ]
    const connections: Connection[] = [
        {
            id: '1',
            outputNode: {id: '1'},
            outputPort: {id: '1'},
            inputNode: {id: '2'},
            inputPort: {id: '2'}
        },
        {
            id: '2',
            outputNode: {id: '2'},
            outputPort: {id: '3'},
            inputNode: {id: '3'},
            inputPort: {id: '4'}
        },
        {
            id: '3',
            outputNode: {id: '3'},
            outputPort: {id: '5'},
            inputNode: {id: '4'},
            inputPort: {id: '6'},
        },
    ]

    return (
        <div style={{background: "whitesmoke"}}>
            <div className="flex flex-row justify-end p-2">
                <ButtonPrimary onClick={() => {
                    onSave({nodes, connections})
                }}>
                    Save
                </ButtonPrimary>

            </div>
            <div className="h-screen">
                <Canvas
                    nodes={nodes}
                    connections={connections}
                    readonly
                />
            </div>
        </div>
    )
}