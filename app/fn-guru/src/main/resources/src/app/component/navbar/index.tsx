import {Avatar, Dropdown, Navbar as Delegate} from "flowbite-react";
import {FC} from "react";
import {DropdownItem} from "flowbite-react/lib/esm/components/Dropdown/DropdownItem";
import {useNavigate} from "react-router-dom";

import imgUrl from '../../../assets/img/hamal.png'

export const Navbar: FC = () => {
    const navigate = useNavigate()
    return (
        <Delegate
            fluid
            rounded
        >
            <Delegate.Brand href="https://fn.guru">
            <span className="self-center whitespace-nowrap">
                fn(guru)
            </span>
            </Delegate.Brand>
            <div className="flex md:order-2">
            </div>
            <Delegate.Collapse>
                {/*<Delegate.Link onClick={() => navigate("/dashboard", {replace: true})}>*/}
                {/*    Dashboard*/}
                {/*</Delegate.Link>*/}
                <Delegate.Link onClick={() => navigate("/play", {replace: true})}>
                    Play
                </Delegate.Link>
                <Delegate.Link onClick={() => navigate("/functions", {replace: true})}>
                    Functions
                </Delegate.Link>
                <Delegate.Link onClick={() => navigate("/execs", {replace: true})}>
                    Runs
                </Delegate.Link>
            </Delegate.Collapse>
        </Delegate>
    );
}