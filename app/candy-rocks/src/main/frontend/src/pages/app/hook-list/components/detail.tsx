import React, {FC, useEffect, useState} from "react";

import {Dialog, DialogContent, DialogFooter, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {HookWithTriggers} from "@/pages/app/hook-list/type.tsx";
import * as z from "zod";
import {useAuth} from "@/hook/auth.ts";
import {useNavigate} from "react-router-dom";
import {useTriggerHookCreate, useTriggerListHook} from "@/hook";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Button} from "@/components/ui/button.tsx";
import {Loader2, Plus} from "lucide-react";
import {Form} from "@/components/ui/form.tsx";
import {TriggerListItem} from "@/types";
import FormFuncSelect from "@/components/form/func-select.tsx";
import FormHttpMethodSelect from "@/components/form/http-method-select.tsx";

type Prop = {
    item: HookWithTriggers
}

type HookTrigger = {}

const Detail: FC<Prop> = ({item}) => {
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [triggerList, setTriggerList] = useState(item.trigger)

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
                    namespaceId={item.hook.namespace.id}
                    hookId={item.hook.id}
                    hookName={item.hook.name}
                    trigger={triggerList}
                    afterAdd={(item) => {
                        window.location.reload()
                    }}
                />
            </CardHeader>
            <CardContent>
                <div className="flex flex-col text-sm leading-6 divide-y divide-gray-100 ">
                    <div className="flex justify-between py-3 gap-x-4">
                        https://hooks.fn.guru/{item.hook.id}
                    </div>
                    <div className="flex flex-col items-start justify-between ">
                        {triggerList.map(trigger => {
                            return (
                                <span className="flex justify-between w-full" key={trigger.id}>
                                    <b>{trigger.hook.method} </b> {trigger.func.name}
                                </span>
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
    namespaceId: string,
    hookId: string;
    hookName: string;
    trigger: Array<TriggerListItem>;
    afterAdd: (triggerId: string) => void
}

const AddTrigger: FC<AddTriggerProps> = ({namespaceId, hookId, hookName, trigger, afterAdd}) => {
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [loading, setLoading] = useState<boolean>(false)
    const [createTrigger, submittedTrigger] = useTriggerHookCreate()

    const formSchema = z.object({
        funcId: z.string().min(1, "Function required"),
        httpMethod: z.string().min(1, "Http method required"),
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            funcId: "",
            httpMethod: ""
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            createTrigger({
                namespaceId: namespaceId,
                funcId: values.funcId,
                name: `${hookName}-${trigger.length + 1}`,
                hookId: hookId,
                hookMethod: values.httpMethod
            })

        } catch (e) {
            console.error(e)
        } finally {
            // setLoading(false)
        }
    }

    useEffect(() => {
        if (submittedTrigger !== null) {
            afterAdd(submittedTrigger.id)
            setOpenDialog(false)
            form.control._reset()
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
                    <DialogHeader>Add trigger</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormFuncSelect name='funcId' form={form}/>
                            <FormHttpMethodSelect name='httpMethod' form={form}/>
                            <Button type="submit">
                                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Add
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}
