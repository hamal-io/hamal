import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC} from "react";

import {cn} from "@/utils"

type Props = {
    className?: string;
    funcId: string;
}
const FuncSelector: FC<Props> = ({className, funcId}) => {
    return (
        <Select value={funcId}>
            <SelectTrigger className={cn("h-8 bg-white", className)}>
                <SelectValue/>
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectLabel>Functions</SelectLabel>
                    <SelectItem value={funcId}>Current Func</SelectItem>
                </SelectGroup>
            </SelectContent>
        </Select>
    )
}

export default FuncSelector