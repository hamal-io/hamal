import {DotsHorizontalIcon} from "@radix-ui/react-icons"
import {Row} from "@tanstack/react-table"

import {Button} from "@/components/ui/button.tsx"
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger,} from "@/components/ui/dropdown-menu.tsx"
import {useNavigate} from "react-router-dom";
import {useContext} from "react";
import {TriggerListItem} from "@/types";
import {GroupLayoutContext} from "@/components/app/layout";

interface Props {
    row: Row<TriggerListItem>
}

export default function ({row}: Props) {
    const {groupId, namespaceId} = useContext(GroupLayoutContext)
    const navigate = useNavigate()

    return (
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button
                    variant="ghost"
                    className="flex h-8 w-8 p-0 data-[state=open]:bg-muted float-right"
                >
                    <DotsHorizontalIcon className="h-4 w-4"/>
                    <span className="sr-only">Open menu</span>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-[160px]">
                <DropdownMenuItem onClick={() => {
                    navigate(`/groups/${groupId}/namespaces/${namespaceId}/schedules/${row.original.id}`)
                }}>View</DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}
