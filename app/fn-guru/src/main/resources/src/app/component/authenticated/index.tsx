import React, {ReactNode} from 'react'
import {Navbar} from "../index.ts";

interface AuthenticatedProps {
    children: ReactNode;
}

const Authenticated: React.FC<AuthenticatedProps> = (props) => (
    <div className="flex flex-col h-screen justify-between">
        <Navbar/>
        {props.children}
    </div>
);

export default Authenticated

