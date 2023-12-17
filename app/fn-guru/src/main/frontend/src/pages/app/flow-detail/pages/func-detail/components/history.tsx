import React, {FC, useState} from "react";
import {Button} from "@/components/ui/button"
import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger,} from "@/components/ui/dialog"
import {CounterClockwiseClockIcon} from "@radix-ui/react-icons";
import {useFuncHistory} from "@/hook";
import {useNavigate} from "react-router-dom";
import {Table} from "@/components/ui/table.tsx";

type Props = {
    funcId: string;
}
const History: FC<Props> = ({funcId}) => {
    const navigate = useNavigate()

    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [getHistory, funcHistory, loading] = useFuncHistory()


    return (
        <Dialog open={openDialog} onOpenChange={setOpenDialog}>
            <DialogTrigger asChild>
                <Button variant="secondary">
                    <span className="sr-only">Show history</span>
                    <CounterClockwiseClockIcon className="h-4 w-4"/>
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[800px]">
                <DialogHeader>
                    <DialogTitle>Deployed Versions:</DialogTitle>
                </DialogHeader>
                <Table>
                    TODO-134
                </Table>
                <DialogFooter/>
            </DialogContent>
        </Dialog>
    )
}

export default History;