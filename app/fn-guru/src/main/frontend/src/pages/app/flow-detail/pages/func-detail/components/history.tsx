import React, {FC, useState} from "react";
import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,} from "@/components/ui/dialog"
import {CounterClockwiseClockIcon} from "@radix-ui/react-icons";
import {useFuncHistory} from "@/hook";
import {useNavigate} from "react-router-dom";
import Table from "@/pages/app/flow-detail/pages/func-detail/components/history-components/table.tsx";
import {columns} from "@/pages/app/flow-detail/pages/func-detail/components/history-components/columns.tsx";


type Props = {
    funcId: string;
}
const History: FC<Props> = ({funcId}) => {
    const navigate = useNavigate()

    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [getHistory, funcHistory, loading, error] = useFuncHistory()


    const handleClick = () => {
        getHistory(funcId)
    }


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
                <Table data={funcHistory.deployments} columns={columns}/>
                <DialogFooter/>
            </DialogContent>
        </Dialog>
    )
}

export default History;