import React, {useState} from 'react'
import {Button} from 'flowbite-react'
import Editor from "../../../component/editor";
import {invokeAdhoc} from "../../../api/adhoc.ts";
import {Footer, Navbar} from "../../component";


const HomePage: React.FC = () => {
    const [code, setCode] = useState(`log = require('log')\nlog.info("That wasn't hard, was it?")`)
    return (
        <div className="flex flex-col h-screen justify-between">
            <Navbar/>

            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                <div className="flex flex-col items-center justify-center">
                    <p>Simplest way to automate your workflows through cloud functions</p>
                    <p>No installation, no command-line tools.</p>
                    <p>Write code and deploy functions from the browser.</p>
                </div>

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
                            invokeAdhoc({
                                code
                            }).then(submitted => {
                                console.log("id", submitted.id)
                                console.log("reqId", submitted.reqId)
                                console.log(submitted.status)
                            }).catch(error => console.error(error))

                        }}>Test</Button>
                        {/*<Button>Deploy</Button>*/}
                    </div>
                </div>
            </main>

            <Footer/>
        </div>
    );
}

export default HomePage;

