import {Button} from "@/components/ui/button"
import Editor from "@/components/editor.tsx";
import React, {useState} from "react";
import {useApiPost} from "@/hook";
import {useAuth} from "@/hook/auth.ts";
import {PageHeader} from "@/components/page-header.tsx";

export default function PlaygroundPage() {
    const [auth] = useAuth()
    const [post, data] = useApiPost()
    const [code, setCode] = useState("log = require('log')\nlog.info('Let\\'s go..')")

    const Run = () => (
        <Button onClick={() => {
            post(`v1/flows/${auth.defaultFlowIds[auth.groupId]}/adhoc`, {
                inputs: {},
                code: code
            })
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

