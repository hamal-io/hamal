import {LucideIcon} from "lucide-react";
import React, {FC} from "react";

type Props = {
    icon: LucideIcon,
    onClick?: () => void,
}
const MenuItem: FC<Props> = ({icon: Icon, onClick}) => {
    function handleClick() {
        if (onClick) {
            onClick()
        } else {
            console.log("Not implemented yet");
        }
    }

    return (
        <button
            className={"flex flex-row items-center justify-center w-14 h-14 bg-white border-2 rounded-lg hover:text-orange-400"}
            onClick={handleClick}>
            <Icon size={24}/>
        </button>

    )
}

export default MenuItem