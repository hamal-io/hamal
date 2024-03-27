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
import {TriggerListItem} from "@/types";
import {useSetTriggerStatus} from "@/hook";

interface Props {
    row: Row<TriggerListItem>
}

export default function ({row}: Props) {
    const navigate = useNavigate()
    const [setTriggerStatus, setTriggerRequested, loading, error] = useSetTriggerStatus()

    const triggerStatus = row.getValue('status')

    function handleStatusChange() {
        const command = triggerStatus === 'Active' ? 'deactivate' : 'activate'
        const abortController = new AbortController()
        setTriggerStatus(row.getValue('id'), command, abortController)
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
                    navigate(`/schedules/${row.original.id}`)
                }}>View</DropdownMenuItem>
                <DropdownMenuItem onClick={handleStatusChange}>
                    {triggerStatus === 'Active' ? "Deactivate" : "Activate"}
                </DropdownMenuItem>
            </DropdownMenuContent>
        </DropdownMenu>
    )
}