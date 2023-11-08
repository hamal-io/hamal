import React, {ReactNode} from 'react'
import {Navbar} from "../index.ts";
import {useAuth} from "@/hook/auth.ts";

interface AuthenticatedProps {
    children: ReactNode;
}

const Authenticated: React.FC<AuthenticatedProps> = (props) => {
    const [auth, setAuth] = useAuth()
    if (!auth || auth.type === 'Unauthorized') {
        window.location.href = "/"
        setAuth(null)
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

