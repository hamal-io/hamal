import React, {ReactNode} from 'react'
import {Navbar} from "../index.ts";
import {useAuth} from "../../../hook";
import {AUTH_STATE_KEY} from "../../../state.ts";

interface AuthenticatedProps {
    children: ReactNode;
}

const Authenticated: React.FC<AuthenticatedProps> = (props) => {
    const [auth] = useAuth()
    if (!auth || auth.type === 'Unauthorized') {
        window.location.href = "/"
        localStorage.removeItem(AUTH_STATE_KEY)
        return null
    }
    return (
        <div className="flex flex-col h-screen justify-between">
            <Navbar/>
            {props.children}
        </div>
    );
}

export default Authenticated

