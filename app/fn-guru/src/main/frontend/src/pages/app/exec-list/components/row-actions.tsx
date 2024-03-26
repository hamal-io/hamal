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
import {useSetTriggerStatus} from "@/hook";
import {FC} from "react";

interface Props {
    row: Row<ExecListItem>
}

export default function ({row}: Props) {
    const navigate = useNavigate()
    const trigger = row.getValue<ExecTriggerItem>("trigger")

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
                {trigger && <TriggerSwitch triggerId={trigger.id} status={trigger.status}/>}
            </DropdownMenuContent>
        </DropdownMenu>
    )
}


type TriggerSwitchProps = { triggerId: string, status: string }
const TriggerSwitch: FC<TriggerSwitchProps> = ({triggerId, status}) => {
    const [setTriggerStatus, setTriggerRequested, loading, error] = useSetTriggerStatus()
    const current = status === "Active"

    function handleClick(e) {
        e.stopPropagation()
        const abortController = new AbortController()
        setTriggerStatus(triggerId, !current, abortController)
        return (() => abortController.abort())
    }

    return (
        <DropdownMenuItem onClick={handleClick}>
            {current === false ? "Activate" : "Deactivate" }
        </DropdownMenuItem>
    )
}