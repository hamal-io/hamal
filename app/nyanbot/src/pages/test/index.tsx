import React from "react";
import {useAuth} from "@/hook/auth.ts";
import {Editor} from "@/components/nodes/editor.tsx";
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
            nodes={[]}
            connections={[]}
            controls={[]}
        />
    )
}