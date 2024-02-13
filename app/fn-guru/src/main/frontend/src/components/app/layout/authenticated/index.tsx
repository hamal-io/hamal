import React, {ReactNode} from 'react'
import {useAuth} from "@/hook/auth.ts";
import Feedback from "@/components/app/feedback.tsx";

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
        <main className="flex flex-col">
            {props.children}
            {/*<Feedback/>*/}
        </main>
    );
}

export default Authenticated
