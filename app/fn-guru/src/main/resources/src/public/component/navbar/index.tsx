import {Button, Navbar as Delegate} from "flowbite-react";
import React, {FC} from "react";
import {useAuth} from "../../../hook";
import {HiOutlineChip} from "react-icons/hi";
import {useNavigate} from "react-router-dom";

export const Navbar: FC = () => (
    <Delegate
        className="bg-gray-200"
    >
        <Delegate.Brand href="https://fn.guru">
            <span className="self-center whitespace-nowrap">
                fn(guru)
            </span>
        </Delegate.Brand>

        <Delegate.Toggle/>
        <Delegate.Collapse>
            <GoToApp/>
        </Delegate.Collapse>
    </Delegate>
);


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
        <Delegate.Link href="/login">
            Login
        </Delegate.Link>
    )
}