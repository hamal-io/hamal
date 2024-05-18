import * as React from "react"
import {FC} from "react"
import {DotsHorizontalIcon} from "@radix-ui/react-icons"

import {
    AlertDialog,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "@/components/ui/alert-dialog.tsx"
import {Button} from "@/components/ui/button.tsx"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu.tsx"

import {useFuncUpdate} from "@/hook";
import {useFuncDeployLatestCode, useFuncInvoke} from "@/hook/func.ts";

type Props = {
    funcId: string;
    code: string;
}

const Actions: FC<Props> = ({funcId, code}) => {
    const [showDeleteDialog, setShowDeployDialog] = React.useState(false)
    const [updateFunc] = useFuncUpdate()
    const [deployFunc] = useFuncDeployLatestCode()
    const [invokeFunc] = useFuncInvoke()

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
                        onSelect={() => {
                            updateFunc(funcId, null, code)
                            setTimeout(() => {
                                invokeFunc(funcId)
                            }, 500)
                        }}
                    >
                        Test
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        onSelect={() => setShowDeployDialog(true)}
                        className="text-red-600"
                    >
                        Deploy
                    </DropdownMenuItem>
                </DropdownMenuContent>
            </DropdownMenu>
            <AlertDialog open={showDeleteDialog} onOpenChange={setShowDeployDialog}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                        <AlertDialogDescription>
                            The latest code version will be deployednpm . This might take a couple of seconds.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <Button
                            variant="destructive"
                            onClick={() => {
                                deployFunc(funcId, "Not implemented")
                                setShowDeployDialog(false)
                            }}
                        >
                            Deploy
                        </Button>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </>
    )
}

export default Actions