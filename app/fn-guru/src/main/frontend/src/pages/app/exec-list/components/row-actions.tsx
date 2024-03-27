import {DotsHorizontalIcon} from "@radix-ui/react-icons"
import {Row} from "@tanstack/react-table"

import {Button} from "@/components/ui/button.tsx"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu.tsx"
import {useNavigate} from "react-router-dom";
import {ExecListItem, ExecTriggerItem} from "@/types";
import {FC} from "react";
import {useSetTriggerStatus} from "@/hook";

interface Props {
    row: Row<ExecListItem>
}

export default function ({row}: Props) {
    const navigate = useNavigate()
    const [setTriggerStatus, setTriggerRequested, loading, error] = useSetTriggerStatus()

    const trigger = row.getValue<ExecTriggerItem>("trigger")

    function handleStatusChange() {
        const command = trigger.status === 'Active' ? 'deactivate' : 'activate'
        const abortController = new AbortController()
        setTriggerStatus(trigger.id, command, abortController)
        return (() => abortController.abort())
    }

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
                    navigate(`/executions/${row.original.id}`)
                }}>View</DropdownMenuItem>
                {trigger &&
                    <DropdownMenuItem onClick={handleStatusChange}>
                        {trigger.status === 'Active' ? "Deactivate" : "Activate"}
                    </DropdownMenuItem>}
            </DropdownMenuContent>
        </DropdownMenu>
    )
}


