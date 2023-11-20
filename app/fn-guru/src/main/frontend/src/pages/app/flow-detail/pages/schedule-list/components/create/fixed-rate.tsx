import {useNavigate} from "react-router-dom";
import React, {FC, useEffect, useState} from "react";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {ChevronDownIcon, Loader2, Plus} from "lucide-react";
import {useAuth} from "@/hook/auth.ts";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button, buttonVariants} from "@/components/ui/button.tsx";
import {useTriggerFixedRateCreate} from "@/hook";
import FormFuncSelect from "@/components/form/func-select.tsx";

type FlowProps = {
    id: string;
    name: string;
}


type Prop = {
    flow: FlowProps
}

const formSchema = z.object({
    name: z.string().min(2).max(50),
    funcId: z.string().min(1, "Function required"),
    rate: z.number().min(1)
})

const CreateFixedRate: FC<Prop> = ({flow}) => {
    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)

    const [createTrigger, submittedTrigger] = useTriggerFixedRateCreate()
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            rate: 300,
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            createTrigger(
                flow.id,
                values.funcId,
                values.name,
                "PT" + values.rate + 'S'
            )
        } catch (e) {
            console.error(e)
        } finally {
            // setLoading(false)
        }
    }

    useEffect(() => {
        if (openDialog === false) {
            form.control._reset()
        }
    }, [openDialog]);

    useEffect(() => {
        if (submittedTrigger !== null) {
            setOpenDialog(false)
            window.location.reload()
        }
    }, [submittedTrigger, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        <Plus className="w-4 h-4 mr-1"/>
                        New Fixed Rate
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Create Fixed Rate</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Fixed Rate - One" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            This is the name of your trigger.
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />

                            <FormFuncSelect name='funcId' flowId={flow.id} form={form}/>

                            <FormField
                                control={form.control}
                                name="rate"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Rate in seconds</FormLabel>
                                        <FormControl>
                                            <Input
                                                placeholder="300" type="number" {...field}
                                                onChange={event => field.onChange(+event.target.value)}
                                            />
                                        </FormControl>
                                        <FormDescription>
                                            Amount of seconds until function gets invoked again
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormMessage/>
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


export default CreateFixedRate;
