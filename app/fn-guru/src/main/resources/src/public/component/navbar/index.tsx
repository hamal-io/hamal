import {Navbar as Delegate} from "flowbite-react";
import {FC} from "react";

export const Navbar: FC = () => (
    <Delegate
        className="bg-gray-200"
    >
        <Delegate.Brand href="https://fn.guru">
            <span className="self-center whitespace-nowrap">
                fn(guru)
            </span>
        </Delegate.Brand>

        <Delegate.Toggle/>
        <Delegate.Collapse>
            <Delegate.Link href="/login">
                Login
            </Delegate.Link>
        </Delegate.Collapse>
    </Delegate>
);