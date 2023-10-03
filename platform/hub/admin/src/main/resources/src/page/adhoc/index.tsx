import React from 'react'
import {Footer, Navbar} from 'flowbite-react'

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

const AdhocPage: React.FC = () => {
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
                <Navbar.Brand href="https://hamal.io">
                    <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
                        Hamal
                    </span>
                </Navbar.Brand>

                <Navbar.Toggle/>
                <Navbar.Collapse>
                    <Navbar.Link href="/" theme={l}>
                        Dashboard
                    </Navbar.Link>
                    <Navbar.Link href="/adhoc" theme={l}>
                        Adhoc
                    </Navbar.Link>
                    <Navbar.Link href="/functions" theme={l}>
                        Function
                    </Navbar.Link>
                    <Navbar.Link href="/logs" theme={l}>
                        Logs
                    </Navbar.Link>
                    <Navbar.Link href="/triggers" theme={l}>
                        Trigger
                    </Navbar.Link>
                </Navbar.Collapse>
            </Navbar>

            <main className="flex-1 w-full mx-auto p-4 text-lg h-full shadow-lg bg-gray-100">
                <div className="flex flex-col items-center justify-center">
                    <p>Adhoc</p>
                </div>
            </main>

            <Footer container>
                <Footer.Copyright
                    by="hamal.io"
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

export default AdhocPage;

