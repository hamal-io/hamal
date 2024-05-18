import React, {FC, useState} from "react";
import {Button} from "@/components/ui/button.tsx"
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,} from "@/components/ui/dialog.tsx"
import {CounterClockwiseClockIcon} from "@radix-ui/react-icons";
import {useFuncHistory} from "@/hook";
import Table from "@/pages/app/func-detail/components/history-components/table.tsx";
import {columns} from "@/pages/app/func-detail/components/history-components/columns.tsx";

type Props = {
    funcId: string;
}

const History: FC<Props> = ({funcId}) => {
    const [isLoading, setLoading] = useState(false)
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [getHistory, funcHistory, loading, error] = useFuncHistory()

    const handleClick = () => {
        setLoading(true)
        try {
            getHistory(funcId)
        } catch (e) {
            console.error(e)
        } finally {
            setLoading(false)
            setOpenDialog(false)
        }
    }

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <Dialog open={openDialog} onOpenChange={setOpenDialog}>
            <DialogTrigger asChild>
                <Button variant="secondary" onClick={handleClick}>
                    <span className="sr-only">Show history</span>
                    <CounterClockwiseClockIcon className="h-4 w-4"/>
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[800px]">
                <DialogHeader>
                    <DialogTitle>Deployed Versions:</DialogTitle>
                </DialogHeader>
                {funcHistory && (
                    <Table data={funcHistory.deployments} columns={columns}/>
                )}
                <DialogFooter/>
            </DialogContent>
        </Dialog>
    )
}

export default History;