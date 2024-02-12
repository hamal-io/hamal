import {Button} from "@/components/ui/button"
import Editor from "@/components/editor.tsx";
import React, {useContext, useState} from "react";
import {useAdhoc} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import {useAuth} from "@/hook/auth.ts";
import {useUiState} from "@/hook/ui-state.ts";

export default function PlaygroundPage() {
    const [uiState] = useUiState()
    const [adhoc, data] = useAdhoc()
    const [code, setCode] = useState("log = require('log').create({})\nlog.info('Let\\'s go..')")
    const Run = () => (
        <Button onClick={() => {
            adhoc(uiState.namespaceId, code)
        }}>Play</Button>
    )
    return (
        <div className="h-full pt-4">
            <div className="container flex flex-row justify-between items-center ">
                <PageHeader
                    title="Playground"
                    description="Have fun"
                    actions={[]}
                />
                <div className="flex w-full space-x-2 justify-end">
                    <Run/>
                </div>
            </div>

            <div className="container h-full py-6">
                <div className="bg-white p-4 rounded-sm border-2">
                    <Editor code={code} onChange={setCode}/>
                </div>
            </div>
        </div>
    )
}

