import {CounterClockwiseClockIcon} from "@radix-ui/react-icons"

import {Button} from "@/components/ui/button"
import {Separator} from "@/components/ui/separator"
import Editor from "@/components/ui/editor.tsx";
import {useState} from "react";
import {useApiPost} from "@/hook";
import {useAuth} from "@/hook/auth.ts";

export default function PlaygroundPage() {
    const [auth] = useAuth()
    const [post, data] = useApiPost()
    const [code, setCode] = useState("log = require('log')\nlog.info('Let\\'s go..')")
    return (
        <>
            <div className="hidden h-full flex-col md:flex">
                <div className="container flex flex-col items-start justify-between space-y-2 py-4 sm:flex-row sm:items-center sm:space-y-0 md:h-16">
                    <h2 className="text-lg font-semibold">Playground</h2>
                </div>
                <Separator/>
                <div className="flex h-full flex-col space-y-4">
                    <div>
                        <Editor
                            className="min-h-[100px] flex-1 p-4 md:min-h-[700px] lg:min-h-[700px]"
                            code={code} onChange={(code) => setCode(code)}
                        />
                    </div>
                    <div className="flex items-center justify-center space-x-2">
                        <Button onClick={() => {
                            post(`v1/flows/${auth.defaultFlowIds[auth.groupId]}/adhoc`, {
                                inputs: {},
                                code: code
                            })
                        }}>Execute</Button>
                    </div>
                </div>
            </div>
        </>
    )
}
