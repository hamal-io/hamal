import {ReactSVGElement} from "react";
import {EthSvg} from "@/pages/app/recipe-list/components/images/eth.tsx";

export interface Tag {
    id: number
    name: string,
    icon: ReactSVGElement
}

export const tags: Tag[] = [
    {
        id: 1,
        name: "ETH",
        icon: EthSvg
    }
]