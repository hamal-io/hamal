import React, {ReactNode} from 'react'
import {Navbar} from "../../component";

interface AuthenticatedPageProps {
    children: ReactNode;
}

const AuthenticatedPage: React.FC<AuthenticatedPageProps> = (props) => (
    <div className="flex flex-col h-screen justify-between">
        <Navbar/>
        {props.children}
    </div>
);

export default AuthenticatedPage

