import {FC, useState} from "react";
import {
    DropdownMenu,
    DropdownMenuContent, DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu.tsx";
import {DotsHorizontalIcon} from "@radix-ui/react-icons";
import * as React from "react";
import {Button} from "@/components/ui/button.tsx";
import {useNamespaceAppend, useNamespaceUpdate} from "@/hook";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";
import {Dialog, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Plus} from "lucide-react";
import Update from "@/pages/app/workspace-detail/tab/namespace-list/components/update.tsx";


type Props = { name: string, namespaceId: string, parentId: string }
const NamespaceActions: FC<Props> = ({name, namespaceId, parentId}) => {
    const [open, setOpen] = useState(false);
    const [dialogContent, setDialogContent] = useState(null);

    const closeDialog = () => setOpen(false)
    const handleDialog = (type: string) => {
        if (type === "rename") {
            setDialogContent(<Update currName={name} namespaceId={namespaceId} onClose={closeDialog}></Update>)
        } else if (type === "append") {
            setDialogContent(<Append parentId={parentId} onClose={closeDialog}></Append>)
        } else {
            console.log("no good")
        }
        setOpen(true)
    }

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
                            handleDialog("rename")
                        }}>
                        Rename
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        onClick={() => {
                            handleDialog("append")
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

