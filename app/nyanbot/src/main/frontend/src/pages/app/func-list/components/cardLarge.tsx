import NcDropDown, {NcDropDownItem} from "@/components/ui/dropdown/NcDropDown.tsx";
import {FC, useState} from "react";

export const CardLarge = () => {
    return (
        <div className="container -mt-10 lg:-mt-16">
            <div
                className="relative bg-white dark:bg-neutral-900 dark:border dark:border-neutral-700 p-5 lg:p-8 rounded-3xl md:rounded-[40px] shadow-xl flex flex-col md:flex-row">
                <div className="w-32 lg:w-44 flex-shrink-0 mt-12 sm:mt-0">
                    {/* <NcImage
                        src={nftsImgs[2]}
                        containerClassName="aspect-w-1 aspect-h-1 rounded-3xl overflow-hidden z-0 relative"
                        fill
                        sizes="200px"
                    />*/}
                </div>
                <div className="pt-5 md:pt-1 md:ml-6 xl:ml-14 flex-grow">
                    <div className="max-w-screen-sm ">
                        <h2 className="inline-flex items-center text-2xl sm:text-3xl lg:text-4xl font-semibold">
                            <span>Dony Herrera</span>
                            {/* <VerifyIcon
                                className="ml-2"
                                iconClass="w-6 h-6 sm:w-7 sm:h-7 xl:w-8 xl:h-8"
                            />*/}
                        </h2>
                        <div
                            className="flex items-center text-sm font-medium space-x-2.5 mt-2.5 text-green-600 cursor-pointer">
                  <span className="text-neutral-700 dark:text-neutral-300">
                    4.0xc4c16ac453sa645a...b21a{" "}
                  </span>
                            <svg width="20" height="21" viewBox="0 0 20 21" fill="none">
                                <path
                                    d="M18.05 9.19992L17.2333 12.6833C16.5333 15.6916 15.15 16.9083 12.55 16.6583C12.1333 16.6249 11.6833 16.5499 11.2 16.4333L9.79999 16.0999C6.32499 15.2749 5.24999 13.5583 6.06665 10.0749L6.88332 6.58326C7.04999 5.87492 7.24999 5.25826 7.49999 4.74992C8.47499 2.73326 10.1333 2.19159 12.9167 2.84993L14.3083 3.17493C17.8 3.99159 18.8667 5.71659 18.05 9.19992Z"
                                    stroke="currentColor"
                                    strokeWidth="1.5"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                />
                                <path
                                    d="M12.5498 16.6583C12.0331 17.0083 11.3831 17.3 10.5915 17.5583L9.2748 17.9917C5.96646 19.0583 4.2248 18.1667 3.1498 14.8583L2.08313 11.5667C1.01646 8.25833 1.8998 6.50833 5.20813 5.44167L6.5248 5.00833C6.86646 4.9 7.19146 4.80833 7.4998 4.75C7.2498 5.25833 7.0498 5.875 6.88313 6.58333L6.06646 10.075C5.2498 13.5583 6.3248 15.275 9.7998 16.1L11.1998 16.4333C11.6831 16.55 12.1331 16.625 12.5498 16.6583Z"
                                    stroke="currentColor"
                                    strokeWidth="1.5"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                />
                            </svg>
                        </div>

                        <span className="block mt-4 text-sm text-neutral-500 dark:text-neutral-400">
                  Punk #4786 / An OG Cryptopunk Collector, hoarder of NFTs.
                  Contributing to @ether_cards, an NFT Monetization Platform.
                </span>
                    </div>
                    <div className="mt-4 ">
                        {/*<SocialsList itemClass="block w-7 h-7"/>*/}
                    </div>
                </div>
                <div
                    className="absolute md:static left-5 top-4 sm:left-auto sm:top-5 sm:right-5 flex flex-row-reverse justify-end">
                    <NftMoreDropdown
                        actions={[
                            {
                                id: "report",
                                name: "Report abuse",
                                icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M3 3v1.5M3 21v-6m0 0l2.77-.693a9 9 0 016.208.682l.108.054a9 9 0 006.086.71l3.114-.732a48.524 48.524 0 01-.005-10.499l-3.11.732a9 9 0 01-6.085-.711l-.108-.054a9 9 0 00-6.208-.682L3 4.5M3 15V4.5" />
                </svg>
                `,
                            },
                        ]}
                        containerClassName="w-8 h-8 md:w-10 md:h-10 flex items-center justify-center rounded-full bg-neutral-100 hover:bg-neutral-200 dark:hover:bg-neutral-700 dark:bg-neutral-800 cursor-pointer"
                    />
                    {/* <ButtonDropDownShare
                        className="w-8 h-8 md:w-10 md:h-10 flex items-center justify-center rounded-full bg-neutral-100 hover:bg-neutral-200 dark:hover:bg-neutral-700 dark:bg-neutral-800 cursor-pointer mx-2"
                        panelMenusClass="origin-top-right !-right-5 !w-40 sm:!w-52"
                    />

                    <FollowButton
                        isFollowing={false}
                        fontSize="text-sm md:text-base font-medium"
                        sizeClass="px-4 py-1 md:py-2.5 h-8 md:!h-10 sm:px-6 lg:px-8"
                    />*/}
                </div>
            </div>
        </div>
    )
}

export interface NftMoreDropdownProps {
    containerClassName?: string;
    dropdownPositon?: "up" | "down";
    actions?: NcDropDownItem[];
}

const actionsDefault: NftMoreDropdownProps["actions"] = [
    {
        id: "edit",
        name: "Change price",
        icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
  <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v12m-3-2.818l.879.659c1.171.879 3.07.879 4.242 0 1.172-.879 1.172-2.303 0-3.182C13.536 12.219 12.768 12 12 12c-.725 0-1.45-.22-2.003-.659-1.106-.879-1.106-2.303 0-3.182s2.9-.879 4.006 0l.415.33M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
</svg>
`,
    },
    {
        id: "transferToken",
        name: "Transfer token",
        icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
  <path stroke-linecap="round" stroke-linejoin="round" d="M7.5 21L3 16.5m0 0L7.5 12M3 16.5h13.5m0-13.5L21 7.5m0 0L16.5 12M21 7.5H7.5" />
</svg>
`,
    },
    {
        id: "report",
        name: "Report abuse",
        icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
  <path stroke-linecap="round" stroke-linejoin="round" d="M3 3v1.5M3 21v-6m0 0l2.77-.693a9 9 0 016.208.682l.108.054a9 9 0 006.086.71l3.114-.732a48.524 48.524 0 01-.005-10.499l-3.11.732a9 9 0 01-6.085-.711l-.108-.054a9 9 0 00-6.208-.682L3 4.5M3 15V4.5" />
</svg>
`,
    },
    {
        id: "delete",
        name: "Delete item",
        icon: `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5">
  <path stroke-linecap="round" stroke-linejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
</svg>
`,
    },
];


const NftMoreDropdown: FC<NftMoreDropdownProps> = ({
                                                       containerClassName = "py-1.5 px-3 flex rounded-lg hover:bg-neutral-100 dark:hover:bg-neutral-800 cursor-pointer",
                                                       dropdownPositon = "down",
                                                       actions = actionsDefault,
                                                   }) => {
    const [isEditting, setIsEditting] = useState(false);
    const [isReporting, setIsReporting] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [isTransfering, setIsTransfering] = useState(false);

    const openModalEdit = () => setIsEditting(true);
    const closeModalEdit = () => setIsEditting(false);

    const openModalReport = () => setIsReporting(true);
    const closeModalReport = () => setIsReporting(false);

    const openModalDelete = () => setIsDeleting(true);
    const closeModalDelete = () => setIsDeleting(false);

    const openModalTransferToken = () => setIsTransfering(true);
    const closeModalTransferToken = () => setIsTransfering(false);

    const hanldeClickDropDown = (item: NcDropDownItem) => {
        if (item.href) {
            return;
        }

        if (item.id === "edit") {
            return openModalEdit();
        }
        if (item.id === "report") {
            return openModalReport();
        }
        if (item.id === "delete") {
            return openModalDelete();
        }
        if (item.id === "transferToken") {
            return openModalTransferToken();
        }
        return;
    };


    return (
        <NcDropDown
            className={` ${containerClassName} `}
            data={actions}
            panelMenusClass={
                dropdownPositon === "up"
                    ? "origin-bottom-right bottom-0 "
                    : "origin-top-right !w-44 sm:!w-52"
            }
            onClick={hanldeClickDropDown}
        />
    );

}



