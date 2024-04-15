import {Eth, Telegram} from "@/pages/app/recipe-list/components/icons/icons.tsx";
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
    }
}