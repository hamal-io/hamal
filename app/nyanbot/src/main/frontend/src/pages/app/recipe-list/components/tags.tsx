import {Dextools, Eth, Etherscan, Telegram} from "@/pages/app/recipe-list/components/logos/logos.tsx";
import {TagType} from "@/types/recipe.ts";

export const tags: TagType = {
    ethereum: {
        id: 1,
        name: "Ethereum",
        icon: Eth
    },
    telegram: {
        id: 2,
        name: "Telegram",
        icon: Telegram
    },
    etherscan: {
        id: 3,
        name: "Etherscan",
        icon: Etherscan
    },
    dextools: {
        id: 4,
        name: "Dextools",
        icon: Dextools
    }

}