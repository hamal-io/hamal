import React, {ReactNode} from 'react'
import {useAuth} from "@/hook/auth.ts";
import Header from "@/components/app/header.tsx";

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
            <Header/>
            {props.children}
        </div>
    );
}

export default Authenticated

