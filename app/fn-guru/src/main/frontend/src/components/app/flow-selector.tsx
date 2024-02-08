import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {SelectValue} from "@radix-ui/react-select";
import React, {FC} from "react";

import {cn} from "@/utils"

type Props = {
    className?: string;
    namespaceId: string;
}
const Namespaceselector: FC<Props> = ({className, namespaceId}) => {
    return (
        <Select value={namespaceId}>
            <SelectTrigger className={cn("h-8 bg-white", className)}>
                <SelectValue/>
            </SelectTrigger>
            <SelectContent>
                <SelectGroup>
                    <SelectLabel>Worknamespaces</SelectLabel>
                    <SelectItem value={namespaceId}>Current Namespace</SelectItem>
                </SelectGroup>
            </SelectContent>
        </Select>
    )
}

export default Namespaceselector