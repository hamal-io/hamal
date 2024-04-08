import React, {FC} from "react";
import Logo from "@/components/old-ui/logo";
import MenuBar from "@/components/old-ui/menu/MenuBar.tsx";
import SwitchMode from "@/components/old-ui/switch-mode";
import {MetaMaskButton} from "@/app/metamask.tsx";
import NavigationItem from "@/app/header/navigation.tsx";
import {NavItemType} from "@/components/old-ui/navigation";
import _ from "lodash";

const nextId = _.uniqueId;

export interface LoggedOutProps {
}

const LoggedIn: FC<LoggedOutProps> = () => {
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
                            <Navigation/>
                            <div className="self-center hidden sm:block h-6 border-l border-neutral-300 dark:border-neutral-6000"></div>
                            <div className="flex">
                                <SwitchMode/>
                            </div>
                            <div/>
                            <MetaMaskButton/>
                            <div/>
                        </div>
                        <div className="flex items-center space-x-1 xl:hidden">
                            <MenuBar/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

const discoverMenu: NavItemType[] = [
    {
        id: nextId(),
        href: "/",
        name: "Templates",
    }
];

export const navigation: NavItemType[] = [
    {
        id: nextId(),
        href: "/",
        name: "Discover",
        type: "dropdown",
        children: discoverMenu,
    },
    {
        id: nextId(),
        href: "/documentation",
        name: "Documentation",
    },
];


const Navigation = () => {
    return (
        <ul className=" flex h-full">
            {navigation.map((item) => (
                <NavigationItem key={item.id} menuItem={item}/>
            ))}
        </ul>
    );
}

export default LoggedIn;
