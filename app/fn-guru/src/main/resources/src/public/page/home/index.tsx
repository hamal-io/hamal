import React, {useState} from 'react'
import {Button, Card} from 'flowbite-react'
import Editor from "../../../component/editor";
import {Footer, Navbar} from "../../component";
import {useNavigate} from "react-router-dom";
import {HiOutlineCloudUpload} from "react-icons/hi";

const HomePage: React.FC = () => {
    const navigate = useNavigate()
    const [code, setCode] = useState(`log = require 'log'\nlog.info("That wasn't hard, was it?")`)
    return (
        <div className="flex flex-col h-screen justify-between bg-gray-200">
            <Navbar/>
            <main className="flex-1 w-full mx-auto p-4 h-full shadow-lg ">
                <div className="flex flex-col items-center justify-center">
                    <h1 className="text-2xl font-bold">Simplest way to automate your workflows through cloud functions</h1>
                    {/*<h2 className="text-lg font-bold">No installation, no command-line tools, no yaml.</h2>*/}
                    <h2 className="text-lg text-gray-600">(Served as hot as your coffee)</h2>
                </div>

                <div className="flex flex-col items-center justify-center pt-4">
                    <div className="w-3/4 my-1 text-gray-900 ">
                        <Card>
                            <Editor
                                code={code}
                                onChange={code => {
                                    setCode(code || "")
                                }}
                            />
                        </Card>
                    </div>

                    <div className="pt-3">

                        <Button
                            className="bg-gray-400"
                            color="dark"
                            outline
                            onClick={() => {
                                navigate("/onboarding", {replace: true, state: {type: "Function", code}})
                            }}>
                            <HiOutlineCloudUpload className="mr-2 h-5 w-5"/>
                            Deploy now
                        </Button>
                    </div>

                    <div className="flex flex-col items-center justify-center pt-2">
                        <h2 className="text-lg font-bold">No credit card / registration required</h2>
                        <h2 className="text-md">No installation, no command-line tools, no yaml.</h2>
                    </div>


                    <div className="pt-1.5">
                        <h3 className="text-gray-900"></h3>
                    </div>
                </div>
            </main>

            <Footer/>
        </div>
    );
}

export default HomePage;

