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
import React from "react";

const Deploy = () => {
    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button variant="secondary" disabled>
                    <span className="sr-only">Show history</span>
                    <CounterClockwiseClockIcon className="h-4 w-4"/>
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[800px]">
                <DialogHeader>
                    <DialogTitle>History</DialogTitle>
                    <DialogDescription>
                        Inspect previous versions of your function
                    </DialogDescription>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                    <h1> Show history TBD </h1>
                </div>
                <DialogFooter>
                    <Button type="submit">Deploy</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

export default Deploy;