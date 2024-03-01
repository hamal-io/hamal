import React, {ReactNode, useEffect} from 'react'
import {useAuth} from "@/hook/auth.ts";

interface AuthenticatedProps {
    children: ReactNode;
}

const Authenticated: React.FC<AuthenticatedProps> = (props) => {
    const [auth] = useAuth()
    useEffect(() => {
        if (!auth || auth.type === 'Unauthorized') {
            window.location.href = "/"
        }
    }, [auth]);
    return (
        <main className="flex flex-col">
            {props.children}
            {/*<Feedback/>*/}
        </main>
    );
}

export default Authenticated

