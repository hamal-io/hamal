import {Navbar as Delegate} from "flowbite-react";
import {FC} from "react";
import {useNavigate} from "react-router-dom";

export const Navbar: FC = () => {
    const navigate = useNavigate()
    return (
        <Delegate
            fluid
            rounded
        >
            <Delegate.Brand>
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
                <Delegate.Link onClick={() => navigate("/namespaces", {replace: true})}>
                    Namespace
                </Delegate.Link>
            </Delegate.Collapse>
        </Delegate>
    );
}