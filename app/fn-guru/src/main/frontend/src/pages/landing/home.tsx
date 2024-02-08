import React, {useState} from 'react'
import Editor from "@/components/editor.tsx";

import Footer from '@/components/landing/footer.tsx'
import Header from '@/components/landing/header.tsx'


import {useNavigate} from "react-router-dom";
import {useAuth} from "@/hook/auth.ts";
import {Button} from "@/components/ui/button.tsx";

const HomePage: React.FC = () => {
    const [auth] = useAuth()
    const navigate = useNavigate()
    const [code, setCode] = useState(`log = require('log').create()\nlog.info("That wasn't hard, was it?")`)
    return (
        <main className="flex flex-col min-h-screen w-full items-center justify-start p-8 ">
            <Header/>
            {/*<Card className="mx-auto flex w-full max-w-4xl flex-1 flex-col items-start justify-center">*/}
            {/*    <CardHeader className="w-full">*/}
            {/*        <div className="flex w-full flex-col items-center justify-center">*/}
            {/*            <h1 className="text-2xl font-bold">Simplest way to automate your workflows through cloud functions</h1>*/}
            {/*        </div>*/}
            {/*    </CardHeader>*/}
            {/*    <CardContent className="w-full p-8 bg-gray-200 ">*/}
            {/*        <div className="bg-white p-4 rounded-sm">*/}
            {/*            <Editor*/}
            {/*                code={code}*/}
            {/*                onChange={code => {*/}
            {/*                    setCode(code || "")*/}
            {/*                }}*/}
            {/*            />*/}
            {/*        </div>*/}
            {/*    </CardContent>*/}
            {/*    <CardFooter className="w-full justify-center items-center pt-4">*/}
            {/*        <div className="flex flex-col ">*/}
            {/*            <Button*/}
            {/*                onClick={() => {*/}
            {/*                    navigate("/onboarding", {replace: true, state: {type: "Function", code}})*/}
            {/*                }}>*/}
            {/*                Deploy now*/}
            {/*            </Button>*/}

            {/*            <div className="flex flex-col items-center justify-center pt-2">*/}
            {/*                <h4 className="text-lg font-semibold text-gray-500">No credit card / registration required</h4>*/}
            {/*                <h5 className="text-md text-gray-400">No installation, no command-line tools, no yaml.</h5>*/}
            {/*            </div>*/}
            {/*        </div>*/}

            {/*    </CardFooter>*/}
            {/*</Card>*/}

            <div className="flex w-full flex-col pt-16 items-center justify-center">
                <h1 className="text-2xl font-bold">Simplest way to automate your workflows through cloud functions</h1>
            </div>

            <div className="container h-full py-6 max-w-4xl">
                <div className="bg-white p-4 rounded-sm border-2">
                    <Editor code={code} onChange={setCode}/>
                </div>
            </div>

            <div className="flex flex-col ">
                <Button
                    onClick={() => {
                        navigate("/onboarding", {replace: true, state: {type: "Function", code}})
                    }}>
                    Deploy now
                </Button>

                <div className="flex flex-col items-center justify-center pt-2 pb-16">
                    <h4 className="text-md font-semibold text-gray-800">No credit card / registration required</h4>
                    <h5 className="text-sm text-gray-400">No installation, no command-line tools, no YAML.</h5>
                </div>
            </div>
            <Footer/>
        </main>
    );
}

export default HomePage;

