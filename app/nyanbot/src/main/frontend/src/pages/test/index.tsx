import React from "react";
import {useAuth} from "@/hooks/auth.ts";
import {Editor} from "@/components/nodes/editor.tsx";
import {ControlInit, ControlInvoke, ControlText, Graph, Node} from "@/components/nodes/types.ts";

export const TestPage = () => {
    const [auth] = useAuth()
    return (
        <Editor
            onSave={(graph: Graph) => {

                console.log(graph)

                fetch(`${import.meta.env.VITE_BASE_URL}/v1/adhoc`, {
                    method: "POST",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${auth.token}`
                    },
                    body: JSON.stringify({
                        code: JSON.stringify(graph)
                    })
                })
                    .then(response => {

                    })
                    .catch(error => {
                        console.error(error)
                    })

            }}
            nodes={[
                {
                    id: '1',
                    type: 'INIT',
                    title: 'Init',
                    position: {x: -200, y: -400},
                    size: {width: 100, height: 100},
                    outputs: [{
                        id: '1',
                        outputType: 'TypeString'
                    }]
                } satisfies Node,
                {
                    id: '2',
                    type: "TELEGRAM_SEND_MESSAGE",
                    title: 'Telegram - Send Message',
                    position: {x: 0, y: -400},
                    size: {width: 250, height: 300},
                    outputs: []
                } satisfies Node
            ]}
            connections={[
                {
                    id: '1',
                    outputNode: {id: '1'},
                    outputPort: {id: '1'},
                    inputNode: {id: '2'},
                    inputPort: {id: '4'}
                },
            ]}
            controls={[
                {
                    id: '1',
                    type: 'Init',
                    nodeId: '1',
                    selector: 'NO_VALUE',
                    description: 'Let me trigger TG for ya!',
                } satisfies ControlInit,
                {
                    id: '3',
                    type: 'Invoke',
                    nodeId: '2',
                    port: {id: '4'},
                } satisfies ControlInvoke,
                {
                    id: '4',
                    type: 'InputString',
                    nodeId: '2',
                    port: {
                        id: '5',
                        inputType: 'TypeString'
                    },
                    defaultValue: '',
                    placeholder: 'chat_id'
                } satisfies ControlText,
                {
                    id: '5',
                    type: 'InputString',
                    nodeId: '2',
                    port: {
                        id: '6',
                        inputType: 'TypeString'
                    },
                    defaultValue: '',
                    placeholder: 'message'
                } satisfies ControlText
            ]}
        />
    )
}

//
// const nodes: Node[] = [
//     {
//         id: '1',
//         type: "Init",
//         title: "Init",
//         position: {x: -500, y: 0},
//         size: {width: 200, height: 300},
//         controls: [],
//         outputs: [{
//             id: '1'
//         }]
//     } satisfies Node,
//     {
//         id: '2',
//         type: "Select",
//         title: 'Select LP',
//         position: {x: -150, y: 0},
//         size: {width: 250, height: 300},
//         controls: [
//             {
//                 id: '1',
//                 type: 'Input',
//                 ports: [{
//                     id: '2'
//                 }],
//             } satisfies ControlInput,
//             {
//                 id: '2',
//                 type: 'Condition',
//                 ports: [],
//             } satisfies ControlCondition,
//         ],
//         outputs: [{
//             id: '3'
//         }]
//     } satisfies Node,
//
//     {
//         id: '3',
//         type: "ToText",
//         title: 'LP to text',
//         position: {x: 200, y: 0},
//         size: {width: 250, height: 300},
//         controls: [
//             {
//                 id: '1',
//                 type: 'Input',
//                 ports: [{
//                     id: '4'
//                 }],
//             } satisfies ControlInput,
//             {
//                 id: '2',
//                 type: 'Text',
//                 ports: [],
//                 text: `{contract.address} has {total_holder}`,
//                 placeholder: 'Turn into text'
//             } satisfies ControlText,
//         ],
//         outputs: [{
//             id: '5'
//         }]
//     } satisfies Node,
//
//     {
//         id: '4',
//         type: "TelegramMessageSend",
//         title: 'Telegram send message',
//         position: {x: 550, y: 0},
//         size: {width: 250, height: 300},
//         controls: [
//             {
//                 id: '3',
//                 type: 'Text',
//                 ports: [],
//                 text: '',
//                 placeholder: 'chat_id'
//             } satisfies ControlText,
//
//             {
//                 id: '4',
//                 type: 'Text',
//                 ports: [
//                     {
//                         id: '6'
//                     }
//                 ],
//                 text: '',
//                 placeholder: 'message'
//             } satisfies ControlText
//         ],
//         outputs: []
//     } satisfies Node
//
// ]
// const connections: Connection[] = [
//     {
//         id: '1',
//         outputNode: {id: '1'},
//         outputPort: {id: '1'},
//         inputNode: {id: '2'},
//         inputPort: {id: '2'}
//     },
//     {
//         id: '2',
//         outputNode: {id: '2'},
//         outputPort: {id: '3'},
//         inputNode: {id: '3'},
//         inputPort: {id: '4'}
//     },
//     {
//         id: '3',
//         outputNode: {id: '3'},
//         outputPort: {id: '5'},
//         inputNode: {id: '4'},
//         inputPort: {id: '6'},
//     },
// ]
//


// fetch(`${import.meta.env.VITE_BASE_URL}/v1/flows`, {
//     method: "POST",
//     headers: {
//         'Accept': 'application/json',
//         'Content-Type': 'application/json',
//         'Authorization': `Bearer ${auth.token}`
//     },
//     body: JSON.stringify({
//         name: 'dummy flow',
//         triggerType: 'v1_web3_new_lp_pair',
//         code: JSON.stringify(
//             {
//                 "nodes": [],
//                 "connections": []
//             }
//         )
//     })
// })
//     .then(response => {
//
//     })
//     .catch(error => {
//         console.error(error)
//     })