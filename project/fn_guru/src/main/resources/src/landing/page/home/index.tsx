import React, {useState} from 'react'
import {Button, Footer, Navbar} from 'flowbite-react'
import Editor from "../../../component/editor";
import {invokeAdhoc} from "../../../api/adhoc.ts";

const l = {
    base: "block py-2 pr-4 pl-3 md:p-0 text-xl",
    active: {
        on: "bg-green-700 text-white dark:text-white md:bg-transparent md:text-cyan-700",
        off: "border-b border-gray-100 text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white"
    },
    disabled: {
        on: "text-gray-400 hover:cursor-not-allowed dark:text-gray-600",
        off: ""
    }
};

const HomePage: React.FC = () => {
    const [code, setCode] = useState(`log = require('log')\nlog.info("That wasn't hard, was it?")`)
    return (
        <div className="flex flex-col h-screen justify-between">
            <Navbar
                theme={{
                    base: "bg-white px-2 py-5",
                    rounded: {
                        on: "rounded",
                        off: ""
                    },
                    bordered: {
                        on: "border",
                        off: ""
                    },
                    inner: {
                        base: "mx-auto flex flex-wrap items-center justify-between",
                        fluid: {
                            on: "",
                            off: "container"
                        }
                    }
                }}
            >
                <Navbar.Brand href="https://flowbite-react.com">
                    <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
                        fn(guru)
                    </span>
                </Navbar.Brand>

                <Navbar.Toggle/>
                <Navbar.Collapse>
                    <Navbar.Link href="/sign-in" theme={l}>
                        Login
                    </Navbar.Link>
                    <Navbar.Link href="/sign-up" active theme={{
                        base: "px-1 py-1 text-lg",
                        active: {
                            on: "bg-green-700 text-white rounded",
                            off: "border-b border-gray-100 text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white"
                        }
                    }}>
                        Deploy
                    </Navbar.Link>
                </Navbar.Collapse>
            </Navbar>

            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                <div className="flex flex-col items-center justify-center">
                    <p>Simplest way to create Cloud Functions</p>
                    <p>No installation, no command-line tools.</p>
                    <p>Write code and deploy functions from the browser.</p>
                </div>

                <div className="flex flex-col items-center justify-center">
                    <div className="w-1/2">
                        <Editor
                            code={code}
                            onChange={code => {
                                setCode(code || "")

                            }
                            }
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

                        }}>Test</Button>
                        {/*<Button>Deploy</Button>*/}
                    </div>
                </div>


            </main>

            <Footer container>
                <Footer.Copyright
                    by="fn.guru"
                    href="#"
                    year={2023}
                />
                <Footer.LinkGroup>
                    <Footer.Link href="#">
                        About
                    </Footer.Link>
                    <Footer.Link href="#">
                        Privacy Policy
                    </Footer.Link>
                    <Footer.Link href="#">
                        Licensing
                    </Footer.Link>
                    <Footer.Link href="#">
                        Contact
                    </Footer.Link>
                </Footer.LinkGroup>
            </Footer>
        </div>
    );
}

export default HomePage;

