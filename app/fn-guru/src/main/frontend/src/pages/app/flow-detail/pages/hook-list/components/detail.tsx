import React, {FC, useEffect, useState} from "react";

import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {HookWithTriggers} from "@/pages/app/flow-detail/pages/hook-list/type.tsx";
import * as z from "zod";
import {useAuth} from "@/hook/auth.ts";
import {useNavigate} from "react-router-dom";
import {useHookCreate, useTriggerHookCreate} from "@/hook";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/ui/button.tsx";
import {Loader2, Plus} from "lucide-react";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import FormFuncSelect from "@/pages/app/flow-detail/components/form-func-select.tsx";

type Prop = {
    item: HookWithTriggers
}

const Detail: FC<Prop> = ({item}) => {
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    return (
        <Card
            key={item.hook.id}
            className="relative overflow-hidden duration-500 hover:border-primary group"
        >
            <CardHeader>
                <div className="flex items-center justify-between ">
                    <CardTitle>{item.hook.name}</CardTitle>
                </div>
                <AddTrigger
                    flowId={item.hook.flow.id}
                    hookId={item.hook.id}
                    hookName={item.hook.name}
                />
            </CardHeader>
            <CardContent>
                <div className="flex flex-col text-sm leading-6 divide-y divide-gray-100 ">
                    <div className="flex justify-between py-3 gap-x-4">
                        https://hooks.fn.guru/{item.hook.id}
                    </div>
                    <div className="flex flex-col items-start justify-between ">
                        {item.trigger.map(trigger => {
                            return (
                                <div className="flex" key={trigger.id}>
                                    {trigger.name} - {trigger.func.name} - {trigger.hook.methods.join(',')}
                                </div>
                            )
                        })}
                    </div>
                </div>
            </CardContent>
        </Card>
    )
}


export default Detail;


type AddTriggerProps = {
    flowId: string,
    hookId: string;
    hookName: string;
}

const AddTrigger: FC<AddTriggerProps> = ({flowId, hookId, hookName}) => {
    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)

    const [createTrigger, submittedTrigger] = useTriggerHookCreate()

    const formSchema = z.object({
        name: z.string().min(2).max(50),
        funcId: z.string().min(1, "Function required"),
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            funcId: "",
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        // Do something with the form values.
        // ✅ This will be type-safe and validated.

        try {
            createTrigger(flowId, values.funcId, `${hookName}-${values.name}`, hookId)
        } catch (e) {
            console.error(e)
        } finally {
            // setLoading(false)
        }

    }

    useEffect(() => {
        if (submittedTrigger !== null) {
            setOpenDialog(false)

        }
    }, [submittedTrigger, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        <Plus className="w-4 h-4 mr-1"/>
                        Add Trigger
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
                                            This is the name of your trigger.
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />

                            <FormFuncSelect flowId={flowId} form={form}/>

                            <Button type="submit">
                                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Add
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}
