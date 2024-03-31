import React, {FC, Fragment} from "react";
import {avatarColors} from "@/contains/contants.ts";
import ImageW from "@/types/image.tsx";

import {Popover, Transition} from "@headlessui/react";
import LinkW from "@/types/link.tsx";
import logoImg from "@/images/placeholder.png";


interface AvatarProps {
    containerClassName?: string;
    sizeClass?: string;
    radius?: string;
    imgUrl?: string;
    userName?: string;
    hasChecked?: boolean;
    hasCheckedClass?: string;
}

const Avatar: FC<AvatarProps> = ({
                                     containerClassName = "ring-1 ring-white dark:ring-neutral-900",
                                     sizeClass = "h-6 w-6 text-sm",
                                     radius = "rounded-full",
                                     imgUrl = "",
                                     userName,
                                     hasChecked,
                                     hasCheckedClass = "w-4 h-4 bottom-1 -right-0.5",
                                 }) => {

    const url = imgUrl || "http://nyanbot.com";
    const name = userName || "John Doe";
    const _setBgColor = (name: string) => {
        const backgroundIndex = Math.floor(
            name.charCodeAt(0) % avatarColors.length
        );
        return avatarColors[backgroundIndex];
    };

    return (
        <div
            className={`wil-avatar relative flex-shrink-0 inline-flex items-center justify-center text-neutral-100 uppercase font-semibold shadow-inner ${radius} ${sizeClass} ${containerClassName}`}
            style={{backgroundColor: url ? undefined : _setBgColor(name)}}
        >
            {url && (
                <ImageW
                    sizes="100px"
                    className={`absolute inset-0 w-full h-full object-cover ${radius}`}
                    src={logoImg}
                    alt={name}
                />
            )}
            <span className="wil-avatar__name">{name[0]}</span>

            {hasChecked && (
                <span className={`  text-white  absolute  ${hasCheckedClass}`}>
        </span>
            )}
        </div>
    );
};


export const Profile = () => {
    return (
        <div className="AvatarDropdown relative flex">
            <Popover className="self-center">
                {({open}) => (
                    <>
                        <Popover.Button
                            className={`inline-flex items-center focus:outline-none focus-visible:ring-2 focus-visible:ring-white focus-visible:ring-opacity-75`}
                        >
                            <Avatar
                                sizeClass="w-8 h-8 sm:w-9 sm:h-9"
                            />
                        </Popover.Button>
                        <Transition
                            as={Fragment}
                            enter="transition ease-out duration-200"
                            enterFrom="opacity-0 translate-y-1"
                            enterTo="opacity-100 translate-y-0"
                            leave="transition ease-in duration-150"
                            leaveFrom="opacity-100 translate-y-0"
                            leaveTo="opacity-0 translate-y-1"
                        >
                            <Popover.Panel className="absolute z-10 w-screen max-w-[260px] px-4 top-full -right-2 sm:right-0 sm:px-0">
                                <div className="overflow-hidden rounded-3xl shadow-lg ring-1 ring-black ring-opacity-5">
                                    <div className="relative grid grid-cols-1 gap-6 bg-white dark:bg-neutral-800 py-7 px-6">
                                        <div className="flex items-center space-x-3">
                                            {/*<Avatar imgUrl={avatarImgs[7]} sizeClass="w-12 h-12" />*/}
                                            <Avatar sizeClass="w-12 h-12"/>

                                            <div className="flex-grow">
                                                <h4 className="font-semibold">TO BE DONE</h4>
                                                <p className="text-xs mt-0.5">0xsome...address</p>
                                            </div>
                                        </div>

                                        <div className="w-full border-b border-neutral-200 dark:border-neutral-700"/>

                                        <LinkW
                                            href={"/"}
                                            className="flex items-center p-2 -m-3 transition duration-150 ease-in-out rounded-lg hover:bg-neutral-100 dark:hover:bg-neutral-700 focus:outline-none focus-visible:ring focus-visible:ring-orange-500 focus-visible:ring-opacity-50"
                                        >
                                            <div className="flex items-center justify-center flex-shrink-0 text-neutral-500 dark:text-neutral-300">
                                                <svg
                                                    width="24"
                                                    height="24"
                                                    viewBox="0 0 24 24"
                                                    fill="none"
                                                    xmlns="http://www.w3.org/2000/svg"
                                                >
                                                    <path
                                                        d="M8.90002 7.55999C9.21002 3.95999 11.06 2.48999 15.11 2.48999H15.24C19.71 2.48999 21.5 4.27999 21.5 8.74999V15.27C21.5 19.74 19.71 21.53 15.24 21.53H15.11C11.09 21.53 9.24002 20.08 8.91002 16.54"
                                                        stroke="currentColor"
                                                        strokeWidth="1.5"
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                    />
                                                    <path
                                                        d="M15 12H3.62"
                                                        stroke="currentColor"
                                                        strokeWidth="1.5"
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                    />
                                                    <path
                                                        d="M5.85 8.6499L2.5 11.9999L5.85 15.3499"
                                                        stroke="currentColor"
                                                        strokeWidth="1.5"
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                    />
                                                </svg>
                                            </div>
                                            <div className="ml-4">
                                                <p className="text-sm font-medium ">{"Disconnect"}</p>
                                            </div>
                                        </LinkW>
                                    </div>
                                </div>
                            </Popover.Panel>
                        </Transition>
                    </>
                )}
            </Popover>
        </div>
    );
}
