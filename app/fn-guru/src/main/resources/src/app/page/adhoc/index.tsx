import React, {useState} from 'react'
import {Button} from "flowbite-react";
import {invokeAdhoc} from "../../../api/adhoc.ts";
import Editor from "../../../component/editor";

const AdhocPage: React.FC = () => {
    const [code, setCode] = useState("log = require('log')\nlog.info('Let\\'s go..')")
    return (
        <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
            <div className="flex flex-col items-center justify-center">
                <p> Play </p>

                <div className="w-1/2">
                    <Editor
                        code={code}
                        onChange={code => {
                            setCode(code || "")
                        }}
                    />
                </div>

                <div className="flex flex-row ">
                    <Button onClick={() => {
                        invokeAdhoc({
                            code
                        }).then(submitted => {
                            console.log("id", submitted.id)
                            console.log("reqId", submitted.reqId)
                            console.log(submitted.status)
                        }).catch(error => console.error(error))

                    }}> Run </Button>
                    {/*<Button>Deploy</Button>*/}
                </div>

            </div>
        </main>
    );
}

export default AdhocPage

