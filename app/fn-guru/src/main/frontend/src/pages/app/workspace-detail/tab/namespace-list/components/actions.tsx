import * as React from "react";
import {FC, RefObject, useEffect, useState} from "react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {Dialog} from "@/components/ui/dialog.tsx";
import Update from "@/pages/app/workspace-detail/tab/namespace-list/components/update.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";
import {Button} from "@/components/ui/button.tsx";


type Props = {
    id: string
    name: string
}
const NamespaceActions: FC<Props> = ({id, name}) => {
    const [open, setOpen] = useState(false);
    const [dialogContent, setDialogContent] = useState(null);

    useEffect(() => {
        if (dialogContent) {
            setOpen(true)
        }
    }, [dialogContent]);

    return (
        <>
            <DropdownMenu>
                <DropdownMenuTrigger asChild>
                    <Button variant={"ghost"}>{name}</Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    <DropdownMenuItem
                        onClick={() => {
                            setDialogContent(
                                <Append appendTo={id} onClose={() => setOpen(false)}></Append>
                            )
                        }}>
                        Append
                    </DropdownMenuItem>
                    {/*  <DropdownMenuItem
                        onClick={() => {
                            console.log("not implemented")
                        }}>
                        Choose
                    </DropdownMenuItem>*/}
                    <DropdownMenuItem
                        onClick={() => {
                            setDialogContent(
                                <Update id={id} onClose={() => setOpen(false)}/>
                            )
                        }}>
                        Rename
                    </DropdownMenuItem>
                </DropdownMenuContent>
            </DropdownMenu>
            {dialogContent &&
                <Dialog open={open} onOpenChange={setOpen}>
                    {dialogContent}
                </Dialog>
            }
        </>

    )
}

export default NamespaceActions

