import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC} from "react";

import {cn} from "@/utils"

type Props = {
    className?: string;
    flowId: string;
}
const FlowSelector: FC<Props> = ({className, flowId}) => {
    return (
        <Select value={flowId}>
            <SelectTrigger className={cn("h-8 bg-white", className)}>
                <SelectValue/>
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectLabel>Workflows</SelectLabel>
                    <SelectItem value={flowId}>Current Flow</SelectItem>
                </SelectGroup>
            </SelectContent>
        </Select>
    )
}

export default FlowSelector