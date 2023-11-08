import React, {FC} from "react";

import {HiOutlineChip} from "react-icons/hi";
import {useNavigate} from "react-router-dom";
import {useAuth} from "@/hook/auth.ts";
import {Button} from "@/components/ui/button.tsx";

const Header: FC = () => (
    <header className="sticky top-0 z-40 w-full border-b bg-background">
        <div className="container flex h-16 items-center space-x-4 sm:justify-between sm:space-x-0">
            <Nav/>
            <div className="flex flex-1 items-center justify-end space-x-4">
                <nav className="flex items-center space-x-1">
                    <GoToApp/>
                </nav>
            </div>
        </div>
    </header>
);

function Nav() {
    return (
        <div className="flex gap-6 md:gap-10">
            <a key='home' href="/" className="flex items-center space-x-2">
                <span className="inline-block font-bold">fn(guru)</span>
            </a>
            <nav className="flex gap-6">
            </nav>
        </div>
    )
}


export default Header;

const GoToApp = () => {
    const [auth] = useAuth()
    const navigate = useNavigate()

    if (auth != null && auth.type !== "Unauthorized") {
        return (
            <Button
                className="bg-gray-500"
                color="dark"
                onClick={() => {
                    navigate("/namespaces", {replace: true})
                }}>
                <HiOutlineChip className="mr-2 h-5 w-5"/>
                Let's automate
            </Button>
        )
    }

    return (
        <Button
            onClick={() => {
                navigate("/login", {replace: true})
            }}>
            Login
        </Button>
    )
}