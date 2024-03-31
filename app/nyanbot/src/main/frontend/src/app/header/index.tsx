import React, {FC} from "react";
import LoggedOut from "@/app/header/logged-out.tsx";
import {useAuth} from "@/hooks/auth.ts";
import LoggedIn from "@/app/header/logged-in.tsx";

export interface HeaderLoggedProps {
}

const Header: FC<HeaderLoggedProps> = () => {
    const [auth] = useAuth()

    if (auth.type === 'Unauthorized') {
        return (
            <div className="sticky top-0 w-full left-0 right-0 z-40 bg-white dark:bg-neutral-900 shadow-sm dark:border-b dark:border-neutral-700">
                <LoggedOut/>
            </div>
        );
    }

    return (
        <div className="sticky top-0 w-full left-0 right-0 z-40 bg-white dark:bg-neutral-900 shadow-sm dark:border-b dark:border-neutral-700">
            <LoggedIn/>
        </div>
    );
};

export default Header;
