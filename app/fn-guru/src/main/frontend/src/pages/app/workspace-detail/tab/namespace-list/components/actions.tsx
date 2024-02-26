import * as React from "react";
import {FC, useEffect, useState} from "react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {DotsHorizontalIcon} from "@radix-ui/react-icons";
import {Button} from "@/components/ui/button.tsx";
import {Dialog} from "@/components/ui/dialog.tsx";
import Update from "@/pages/app/workspace-detail/tab/namespace-list/components/update.tsx";
import {NamespaceListItem} from "@/types";


type Props = {
    item: NamespaceListItem
}
const NamespaceActions: FC<Props> = ({item}) => {
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
                    <Button variant="secondary">
                        <span className="sr-only">Actions</span>
                        <DotsHorizontalIcon className="h-4 w-4"/>
                    </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    <DropdownMenuSeparator/>
                    <DropdownMenuItem
                        onClick={() => {
                            console.log("not implemented")
                        }}>
                        Choose
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        onClick={() => {
                            setDialogContent(
                                <Update item={item} onClose={() => setOpen(false)}></Update>
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

