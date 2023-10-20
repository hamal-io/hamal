import {Navbar as Delegate} from "flowbite-react";
import {FC} from "react";

export const Navbar: FC = () => (
    <Delegate>
        <Delegate.Brand href="https://fn.guru">
            <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">
                fn(guru)
            </span>
        </Delegate.Brand>

        <Delegate.Toggle/>
        <Delegate.Collapse>
            <Delegate.Link href="/sign-in">
                Login
            </Delegate.Link>
            <Delegate.Link href="/sign-up">
                Deploy
            </Delegate.Link>
        </Delegate.Collapse>
    </Delegate>
);