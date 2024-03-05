import * as React from "react";
import {Component, FC, useEffect, useState} from "react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {Dialog} from "@/components/ui/dialog.tsx";
import Update from "@/pages/app/workspace-detail/tab/namespace-list/components/update.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";
import Element = React.JSX.Element;


type Props = {
    id: string
    trigger: Element
}
const NamespaceActions: FC<Props> = ({id, trigger}) => {
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
                    {trigger}
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    <DropdownMenuItem
                        onClick={() => {
                            console.log("not implemented")
                        }}
                        disabled={true}
                        >

                        Select
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        onClick={() => {
                            setDialogContent(
                                <Update id={id} onClose={() => setOpen(false)}/>
                            )
                        }}>
                        Rename
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        onClick={() => {
                            setDialogContent(
                                <Append appendTo={id} onClose={() => setOpen(false)}></Append>
                            )
                        }}>
                        Append
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

