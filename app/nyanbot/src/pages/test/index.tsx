import React, {useContext} from "react";
import {useAuth} from "@/hook/auth.ts";
import {ContextEditorState, Editor} from "@/components/nodes/editor.tsx";
import {ControlInit, ControlInvoke, ControlTextArea, Graph, Node} from "@/components/nodes/types.ts";
import {useUiState} from "@/hook/ui.ts";

export const TestPage = () => {
    const [auth] = useAuth()
    const [uiState] = useUiState()

    return (
        <Editor
            onTest={(state) => {

                console.log("Time to run some execution")
                const graph = {
                    nodes: Object.values(state.nodes),
                    connections: Object.values(state.connections),
                    controls: Object.values(state.controls),
                }

                console.log(JSON.stringify(graph, null, 4))

                fetch(`${import.meta.env.VITE_BASE_URL}/v1/namespaces/${uiState.namespaceId}/adhoc`, {
                    method: "POST",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${auth.token}`
                    },
                    body: JSON.stringify({
                        code: JSON.stringify(graph),
                        codeType: "Nodes"
                    })
                })
                    .then(response => {

                    })
                    .catch(error => {
                        console.error(error)
                    })

            }}
            nodes={[
                // {
                //     id: '1',
                //     type: 'Init',
                //     title: 'Init',
                //     position: {x: 0, y: 0},
                //     size: {width: 100, height: 100},
                //     outputs: [{
                //         id: '1',
                //         type: 'String'
                //     }]
                // } satisfies Node,
                // {
                //     id: '2',
                //     type: "Telegram_Send_Message",
                //     title: 'Telegram - Send Message',
                //     position: {x: 200, y: 0},
                //     size: {width: 250, height: 300},
                //     outputs: []
                // } satisfies Node
            ]}
            connections={[
                // {
                //     id: '1',
                //     outputNode: {id: '1'},
                //     outputPort: {id: '1'},
                //     inputNode: {id: '2'},
                //     inputPort: {id: '2'}
                // },
            ]}
            controls={[
                // {
                //     id: '1',
                //     type: 'Init',
                //     nodeId: '1',
                //     config: {
                //         selector: 'No_Value',
                //     },
                //     description: 'Let me trigger TG for ya!',
                // } satisfies ControlInit,
                // {
                //     id: '3',
                //     type: 'Invoke',
                //     nodeId: '2',
                //     port: {id: '2'},
                // } satisfies ControlInvoke,
                // {
                //     id: '4',
                //     type: 'Text_Area',
                //     nodeId: '2',
                //     port: {
                //         id: '3',
                //         type: 'String'
                //     },
                //     value: '',
                //     placeholder: 'chat_id'
                // } satisfies ControlTextArea,
                // {
                //     id: '5',
                //     type: 'Text_Area',
                //     nodeId: '2',
                //     port: {
                //         id: '4',
                //         type: 'String'
                //     },
                //     value: '',
                //     placeholder: 'message'
                // } satisfies ControlTextArea
            ]}
        />
    )
}