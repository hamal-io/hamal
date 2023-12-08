import * as React from "react";
import {FC, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Button} from "@/components/ui/button.tsx";
import * as z from "zod"
import {useFuncDeployLatestCode} from "@/hook/func.ts";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage
} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Loader2} from "lucide-react";
import {useFuncUpdate} from "@/hook";
import {FlowListItem, FuncListItem} from "@/types";


const formSchema = z.object({
    message: z.string().max(50),
})

type Props = {
    flow: FlowListItem
    func: FuncListItem
    code: string;
    name: string;
}

const Deploy: FC<Props> = ({flow, func, code, name}) => {
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)

    const [updateFunc, submittedUpdate] = useFuncUpdate()
    const [deployFunc, submittedDeploy] = useFuncDeployLatestCode()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            message: ""
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        try {
            updateFunc(func.id, name, code)
            deployFunc(func.id, values.message)
        } catch (e) {
            console.error(e)
        }
    }

    useEffect(() => {
        if (submittedUpdate !== null && submittedDeploy !== null) { //update might be null
            navigate(`/flows/${flow.id}/functions/${submittedDeploy.funcId}`)
            setOpenDialog(false)
        }
    }, [flow.id, submittedDeploy, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button variant="secondary">
                        Deploy
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Function Deployment</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="message"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel></FormLabel>
                                        <FormControl>
                                            <Input placeholder="What effects do your changes have?" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            This might take a second. You can return to an earlier version at any time.
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit" variant="destructive">
                                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Save & Deploy
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}

export default Deploy;

