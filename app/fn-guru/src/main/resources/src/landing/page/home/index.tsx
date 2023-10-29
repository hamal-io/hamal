import React, {useState} from 'react'
import {Button, Dropdown} from 'flowbite-react'
import Editor from "../../../component/editor";
import {invokeAdhoc} from "../../../api/adhoc.ts";
import {Footer, Navbar} from "../../component";
import {createAnonymousAccount} from "../../../api/account.ts";
import {useNavigate} from "react-router-dom";


const HomePage: React.FC = () => {
    const navigate = useNavigate()
    const [code, setCode] = useState(`log = require('log')\nlog.info("That wasn't hard, was it?")`)
    return (
        <div className="flex flex-col h-screen justify-between">
            <Navbar/>

            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                <div className="flex flex-col items-center justify-center">
                    <p>Simplest way to automate your workflows through cloud functions</p>
                    <p>No installation, no command-line tools, no yaml.</p>
                    <p>Served as hot as your coffee</p>
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
                            localStorage.setItem('to-deploy', JSON.stringify({
                                type: 'Function',
                                code: code
                            }))

                            navigate("/onboarding", {replace: true})
                        }}
                        >
                            Deploy now
                        </Button>

                        No credit card / signup required

                    </div>
                </div>
            </main>

            <Footer/>
        </div>
    );
}

export default HomePage;

