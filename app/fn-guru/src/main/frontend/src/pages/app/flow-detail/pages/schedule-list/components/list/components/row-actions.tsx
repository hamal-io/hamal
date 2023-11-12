import {DotsHorizontalIcon} from "@radix-ui/react-icons"
import {Row} from "@tanstack/react-table"

import {Button} from "@/components/ui/button.tsx"
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger,} from "@/components/ui/dropdown-menu.tsx"
import {useNavigate} from "react-router-dom";
import {useContext} from "react";
import {FlowContext} from "@/pages/app/flow-detail";
import {TriggerListItem} from "@/hook/api/schedule.tsx";

interface Props {
    row: Row<TriggerListItem>
}

export default function ({row}: Props) {
    const flow = useContext(FlowContext)
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
                    navigate(`/flows/${flow.id}/schedules/${row.original.id}`)
                }}>View</DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}
