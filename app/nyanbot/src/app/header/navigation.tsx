import {Popover, Transition} from "@headlessui/react";
import {ChevronDownIcon} from "@heroicons/react/24/solid";
import React, {FC, Fragment, useEffect, useState} from "react";
import LinkW from "@/types/link.tsx";
import {useLocation} from "react-router-dom";
import {NavItemType} from "@/components/old-ui/navigation";


export interface NavigationItemProps {
    menuItem: NavItemType;
}

type NavigationItemWithRouterProps = NavigationItemProps;

const NavigationItem: FC<NavigationItemWithRouterProps> = ({menuItem}) => {
    const [menuCurrentHovers, setMenuCurrentHovers] = useState<string[]>([]);

    const location = useLocation();
    useEffect(() => {
        setMenuCurrentHovers([]);
    }, [location.pathname]);

    const onMouseEnterMenu = (id: string) => {
        setMenuCurrentHovers((state) => [...state, id]);
    };

    const onMouseLeaveMenu = (id: string) => {
        setMenuCurrentHovers((state) => {
            return state.filter((item, index) => {
                return item !== id && index < state.indexOf(id);
            });
        });
    };

    // ===================== MENU DROPDOW =====================
    const renderDropdownMenu = (menuDropdown: NavItemType) => {
        const isHover = menuCurrentHovers.includes(menuDropdown.id);
        return (
            <Popover
                as="li"
                className={`menu-item flex items-center menu-dropdown relative ${
                    menuDropdown.isNew ? "menuIsNew_lv1" : ""
                }`}
                onMouseEnter={() => onMouseEnterMenu(menuDropdown.id)}
                onMouseLeave={() => onMouseLeaveMenu(menuDropdown.id)}
            >
                {() => (
                    <>
                        <div>{renderMainItem(menuDropdown)}</div>
                        <Transition
                            as={Fragment}
                            show={isHover}
                            enter="transition ease-out duration-150 "
                            enterFrom="opacity-0 translate-y-1"
                            enterTo="opacity-100 translate-y-0"
                            leave="transition ease-in duration-150"
                            leaveFrom="opacity-100 translate-y-0"
                            leaveTo="opacity-0 translate-y-1"
                        >
                            <Popover.Panel
                                static
                                className="sub-menu will-change-transform absolute transform z-10 w-56 top-full left-0"
                            >
                                <ul className="rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 dark:ring-white dark:ring-opacity-10 text-sm relative bg-white dark:bg-neutral-900 py-4 grid space-y-1">
                                    {menuDropdown.children?.map((i) => {
                                        if (i.type) {
                                            return renderDropdownMenuNavLinkHasChild(i);
                                        } else {
                                            return (
                                                <li
                                                    key={i.id}
                                                    className={`px-2 ${i.isNew ? "menuIsNew" : ""}`}
                                                >
                                                    {renderDropdownMenuNavLink(i)}
                                                </li>
                                            );
                                        }
                                    })}
                                </ul>
                            </Popover.Panel>
                        </Transition>
                    </>
                )}
            </Popover>
        );
    };

    const renderDropdownMenuNavLinkHasChild = (item: NavItemType) => {
        const isHover = menuCurrentHovers.includes(item.id);
        return (
            <Popover
                as="li"
                key={item.id}
                className="menu-item flex items-center menu-dropdown relative px-2"
                onMouseEnter={() => onMouseEnterMenu(item.id)}
                onMouseLeave={() => onMouseLeaveMenu(item.id)}
            >
                {() => (
                    <>
                        <div>{renderDropdownMenuNavLink(item)}</div>
                        <Transition
                            as={Fragment}
                            show={isHover}
                            enter="transition ease-out duration-150"
                            enterFrom="opacity-0 translate-y-1"
                            enterTo="opacity-100 translate-y-0"
                            leave="transition ease-in duration-150"
                            leaveFrom="opacity-100 translate-y-0"
                            leaveTo="opacity-0 translate-y-1"
                        >
                            <Popover.Panel
                                static
                                className="sub-menu absolute z-10 w-56 left-full pl-2 top-0"
                            >
                                <ul className="rounded-lg shadow-lg ring-1 ring-black ring-opacity-5 dark:ring-white dark:ring-opacity-10 text-sm relative bg-white dark:bg-neutral-900 py-4 grid space-y-1">
                                    {item.children?.map((i) => {
                                        if (i.type) {
                                            return renderDropdownMenuNavLinkHasChild(i);
                                        } else {
                                            return (
                                                <li key={i.id} className="px-2">
                                                    {renderDropdownMenuNavLink(i)}
                                                </li>
                                            );
                                        }
                                    })}
                                </ul>
                            </Popover.Panel>
                        </Transition>
                    </>
                )}
            </Popover>
        );
    };

    const renderDropdownMenuNavLink = (item: NavItemType) => {
        return (
            <LinkW
                target={item.targetBlank ? "_blank" : undefined}
                rel="noopener noreferrer"
                className="flex items-center font-normal text-neutral-6000 dark:text-neutral-300 py-2 px-4 rounded-md hover:text-neutral-700 hover:bg-neutral-100 dark:hover:bg-neutral-800 dark:hover:text-neutral-200 "
                href={item.href || ""}
            >
                {item.name}
                {item.type && (
                    <ChevronDownIcon
                        className="ml-2 h-4 w-4 text-neutral-500"
                        aria-hidden="true"
                    />
                )}
            </LinkW>
        );
    };

    const renderMainItem = (item: NavItemType) => {
        return (
            <LinkW
                rel="noopener noreferrer"
                className="inline-flex items-center text-sm xl:text-base font-normal text-neutral-700 dark:text-neutral-300 py-2 px-4 xl:px-5 rounded-full hover:text-neutral-900 hover:bg-neutral-100 dark:hover:bg-neutral-800 dark:hover:text-neutral-200"
                href={item.href || "/"}
            >
                {item.name}
                {item.type && (
                    <ChevronDownIcon
                        className="ml-1 -mr-1 h-4 w-4 text-neutral-400"
                        aria-hidden="true"
                    />
                )}
            </LinkW>
        );
    };

    switch (menuItem.type) {
        case "dropdown":
            return renderDropdownMenu(menuItem);
        default:
            return (
                <li className="menu-item flex items-center">
                    {renderMainItem(menuItem)}
                </li>
            );
    }
};

export default NavigationItem;
