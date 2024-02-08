import React, {FC} from "react";

import {Link, useNavigate} from "react-router-dom";
import {useAuth} from "@/hook/auth.ts";
import {Button} from "@/components/ui/button.tsx";

const Header: FC = () => (
    <header
        className={"grid w-full grid-cols-2 gap-2 sm:grid-cols-5 mx-auto w-full max-w-4xl"}
    >
        <div className="flex items-center sm:col-span-1">
            <Link to="/" className="font-cal text-muted-foreground hover:text-foreground">
                fn(guru)
            </Link>
        </div>
        <div className="hidden items-center justify-center sm:col-span-3 sm:flex sm:gap-3">
            <Button variant="link" asChild>
                <Link to="https://docs.fn.guru" target="_blank">
                    Documentation
                </Link>
            </Button>
        </div>
        <div className="flex items-center justify-end gap-3 sm:col-span-1">
            <GoToApp/>
        </div>
    </header>
);
export default Header;

const GoToApp = () => {
    const [auth] = useAuth()
    const navigate = useNavigate()

    if (auth != null && auth.type !== "Unauthorized") {
        return (
            <Button
                onClick={() => {
                    navigate("/groups", {replace: true})
                }}>
                Back to work
            </Button>
        )
    }

    return (
        <Button
            color="dark"
            variant={"secondary"}
            onClick={() => {
                navigate("/login", {replace: true})
            }}>
            Login
        </Button>
    )
}