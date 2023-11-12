import {Button} from "@/components/ui/button"
import Editor from "@/components/editor.tsx";
import React, {useState} from "react";
import {useApiPost} from "@/hook";
import {useAuth} from "@/hook/auth.ts";
import {Card, CardContent, CardFooter} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import {Separator} from "@/components/ui/separator.tsx";

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
        }}>Run</Button>
    )

    return (
        <div className="flex flex-col justify-center items-center">
            <div className="pt-8 px-8">
                <PageHeader title="Playground" description="" actions={[]}/>
            </div>
            <Card className="mx-auto flex w-full max-w-4xl flex-1 flex-col items-start justify-center">
                <CardContent className="w-full p-4 bg-gray-200 ">
                    <div className="bg-white p-4 rounded-sm">
                        <Editor
                            code={code}
                            onChange={code => {
                                setCode(code || "")
                            }}
                        />
                    </div>
                </CardContent>
                <CardFooter className="w-full justify-center items-center pt-4">
                    <div className="flex flex-col ">
                        <Run/>
                    </div>
                </CardFooter>
            </Card>
        </div>
    )
}

