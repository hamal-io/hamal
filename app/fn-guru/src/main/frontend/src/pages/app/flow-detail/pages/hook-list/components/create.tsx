import {useNavigate} from "react-router-dom";
import React, {FC, useEffect, useState} from "react";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {BookOpen, Loader2, Plus,} from "lucide-react";
import {useAuth} from "@/hook/auth.ts";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {FlowListItem} from "@/types";
import {useHookCreate, useTriggerHookCreate} from "@/hook";
import FormFuncSelect from "@/pages/app/flow-detail/components/form-func-select.tsx";

type Prop = {
    flow: FlowListItem
}

const formSchema = z.object({
    name: z.string().min(2).max(50),
    // funcId: z.string().min(1, "Function required"),
})

const Create: FC<Prop> = ({flow}) => {
    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)

    const [createHook, submittedHook] = useHookCreate()

    // const [triggerName, setTriggerName] = useState('')
    // const [triggerFuncId, setTriggerFuncId] = useState('')
    // const [createTrigger, submittedTrigger] = useTriggerHookCreate()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: ""
        },
    })
    //
    // useEffect(() => {
    //     if (submittedHook != null) {
    //         createTrigger(submittedHook.flowId, triggerFuncId, triggerName, submittedHook.hookId)
    //     }
    // }, [submittedHook]);

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        // Do something with the form values.
        // ✅ This will be type-safe and validated.

        try {
            // setTriggerName(values.name)
            // setTriggerFuncId(values.funcId)
            createHook(flow.id, values.name)


        } catch (e) {
            console.error(e)
        } finally {
            // setLoading(false)
        }

    }

    useEffect(() => {
        if (submittedHook !== null) {
            setOpenDialog(false)

        }
    }, [submittedHook, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        <Plus className="w-4 h-4 mr-1"/>
                        New Webhook
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Create webhook</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Webhook-one" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            This is the name of your webhook.
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />

                            {/*<FormFuncSelect flowId={flow.id} form={form}/>*/}

                            <Button type="submit">
                                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Create
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}


export default Create;