import {Navbar as Delegate} from "flowbite-react";
import {FC} from "react";

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

export const Navbar: FC = () => (
    <Delegate
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
        <Delegate.Brand href="https://fn.guru">
                    <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
                        fn(guru)
                    </span>
        </Delegate.Brand>

        <Delegate.Toggle/>
        <Delegate.Collapse>
            <Delegate.Link href="/sign-in" theme={l}>
                Login
            </Delegate.Link>
            <Delegate.Link href="/sign-up" active theme={{
                base: "px-1 py-1 text-lg",
                active: {
                    on: "bg-green-700 text-white rounded",
                    off: "border-b border-gray-100 text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white"
                }
            }}>
                Deploy
            </Delegate.Link>
        </Delegate.Collapse>
    </Delegate>
);