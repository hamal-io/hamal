import React, {FC} from "react";
import Logo from "@/components/ui/logo";
import MenuBar from "@/components/ui/menu/MenuBar.tsx";
import SwitchMode from "@/components/ui/switch-mode";
import {Profile} from "@/app/header/profile.tsx";
import _ from "lodash";

const nextId = _.uniqueId;

export interface LoggedInProps {
}

const LoggedIn: FC<LoggedInProps> = () => {
    return (
        <div className={`nc-MainNav2Logged relative z-10`}>
            <div className="container">
                <div className="h-20 flex justify-between space-x-4 xl:space-x-8">
                    <div className="self-center flex justify-start flex-grow space-x-3 sm:space-x-8 lg:space-x-10">
                        <Logo/>
                        <div className="hidden sm:block flex-grow max-w-xs">

                        </div>
                    </div>
                    <div className="flex-shrink-0 flex justify-end text-neutral-700 dark:text-neutral-100 space-x-1">
                        <div className="hidden xl:flex space-x-2">
                            <div className="self-center hidden sm:block h-6 border-l border-neutral-300 dark:border-neutral-6000"></div>
                            <div className="flex">
                                <SwitchMode/>
                            </div>
                            <div></div>
                            <Profile/>
                        </div>
                        <div className="flex items-center space-x-1 xl:hidden">
                            <Profile/>
                            <MenuBar/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default LoggedIn;
