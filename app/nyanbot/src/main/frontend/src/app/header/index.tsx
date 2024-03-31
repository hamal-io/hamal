import React, {FC} from "react";
import LoggedOut from "@/app/header/logged-out.tsx";

export interface HeaderLoggedProps {
}

const Header: FC<HeaderLoggedProps> = () => {
    return (
        <div className="nc-HeaderLogged sticky top-0 w-full left-0 right-0 z-40 bg-white dark:bg-neutral-900 shadow-sm dark:border-b dark:border-neutral-700">
            {/*<LoggedIn />*/}
            <LoggedOut/>
        </div>
    );
};

export default Header;
