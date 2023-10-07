import React, {useState} from 'react'
import {Button} from 'flowbite-react'
import {Editor} from '../../component'
import {invokeAdhoc} from "../../api/adhoc.ts";

const AdhocPage: React.FC = () => {
    const [code, setCode] = useState(`log = require('log')\nlog.info("That wasn't hard, was it?")`)

    return (
        <div className="flex flex-col items-center justify-center">
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
                    console.log("run code", code)
                    invokeAdhoc({
                        code
                    }).then(submitted => {
                        console.log("id", submitted.id)
                        console.log("reqId", submitted.reqId)
                        console.log(submitted.status)
                    }).catch(error => console.error(error))

                }}>Execute</Button>
            </div>
        </div>
    );
}

export default AdhocPage;

