import * as React from "react"
import {FC} from "react"
import {DotsHorizontalIcon} from "@radix-ui/react-icons"

import {AlertDialog, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,} from "@/components/ui/alert-dialog"
import {Button} from "@/components/ui/button"
import {DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger,} from "@/components/ui/dropdown-menu"
import {useApiDeployLatestCode, useApiPatch} from "@/hook";
import {useApiFuncInvoke} from "@/hook/api/func.ts";

type Props = {
    funcId: string;
    code: string;
}

interface ApiFuncUpdateSubmitted {
    id: string;
    status: string;
    funcId: string;
}


const Actions: FC<Props> = ({funcId, code}) => {
    const [showDeleteDialog, setShowDeployDialog] = React.useState(false)
    const [updateFunc] = useApiPatch<ApiFuncUpdateSubmitted>()
    const [deployFunc] = useApiDeployLatestCode()
    const [invokeFunc] = useApiFuncInvoke()

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
                            updateFunc(`v1/funcs/${funcId}`, {code})
                            invokeFunc(funcId)
                        }}
                    >
                        Run
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
                        <AlertDialogTitle>Are you sure absolutely sure?</AlertDialogTitle>
                        <AlertDialogDescription>
                            The latest code version will be deployed. This might take a couple of seconds.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <Button
                            variant="destructive"
                            onClick={() => {
                                deployFunc(funcId)
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