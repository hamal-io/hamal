import * as React from "react";
import {FC, useState} from "react";
import {Button} from "@/components/ui/button.tsx";
import * as z from "zod"
import {useFuncDeployLatestCode} from "@/hook/func.ts";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Loader2} from "lucide-react";
import {useFuncUpdate} from "@/hook";
import {DialogDescription} from "@radix-ui/react-dialog";


const formSchema = z.object({
    message: z.string().max(256),
})

type Props = {
    funcId: string
    code: string;
}

const Deploy: FC<Props> = ({funcId, code}) => {
    const [openDialog, setOpenDialog] = useState<boolean>(false)
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
        setLoading(true)
        try {
            updateFunc(funcId, null, code)
            setTimeout(() => {
                deployFunc(funcId, values.message)
            }, 500)
        } catch (e) {
            console.error(e)
        } finally {
            setOpenDialog(false)
            setLoading(false)
            form.reset()
        }
    }

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        Deploy
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Function Deployment</DialogHeader>

                    <DialogDescription>
                        <b> Warning:</b> You are about to replace the code of the function.
                        It might take a couple of seconds for the system to pick up.
                    </DialogDescription>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="message"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Message</FormLabel>
                                        <FormControl>
                                            <Input
                                                placeholder="What did you do or why do you want to deploy?" {...field} />
                                        </FormControl>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <div className="flex flex-row justify-between items-center">
                                <Button onClick={() => {
                                    form.reset()
                                    setOpenDialog(false)
                                }} variant="secondary">
                                    Cancel
                                </Button>
                                <Button type="submit" variant="destructive">
                                    {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                    Deploy
                                </Button>
                            </div>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}

export default Deploy;

