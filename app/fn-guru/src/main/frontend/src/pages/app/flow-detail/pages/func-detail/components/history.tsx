import React, {FC, useState} from "react";
import {Button} from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog"
import {CounterClockwiseClockIcon} from "@radix-ui/react-icons";
import {useFuncHistory} from "@/hook";
import {useNavigate} from "react-router-dom";
import {Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger} from "@/components/ui/select.tsx";
import {cn} from "@/utils";
import {SelectValue} from "@radix-ui/react-select";

type Props = {
    flowId: string,
    funcId: string;
}
const History: FC<Props> = ({flowId, funcId}) => {
    const navigate = useNavigate()

    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [getHistory, funcHistory, loading] = useFuncHistory()


    if (loading) {
        return "Loading..."
    }
    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button variant="secondary"  disabled>
                    <span className="sr-only">Show history</span>
                    <CounterClockwiseClockIcon className="h-4 w-4" />
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[800px]">
                <DialogHeader>
                    <DialogTitle>History</DialogTitle>
                    <DialogDescription>
                        Inspect previous versions of your function
                    </DialogDescription>
                </DialogHeader>
                {/* <div className="grid gap-4 py-4">
                    <h1> Show history TBD </h1>
                </div>*/}
                <Select value={selected} onValueChange={
                    (newFuncId) => {
                        navigate(`/flows/${flowId}/functions/${newFuncId}`, {replace: true})
                        setSelected(newFuncId)
                    }
                }>
                    <SelectTrigger className={cn("h-8 bg-white", className)}>
                        <SelectValue/>
                    </SelectTrigger>
                    <SelectContent>
                        <SelectGroup>
                            <SelectLabel>Versions</SelectLabel>
                            {funcHistory.versions.map(func =>
                                <SelectItem key={func.version} value={func.message}>{func.name}</SelectItem>
                            )}
                        </SelectGroup>
                    </SelectContent>
                </Select>


                <DialogFooter/>
            </DialogContent>
        </Dialog>
    )
}

export default History;