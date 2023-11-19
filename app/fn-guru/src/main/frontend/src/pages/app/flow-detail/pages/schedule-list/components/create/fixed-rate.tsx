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
import {ApiFlowSimple} from "@/api/types";
import {cn} from "@/utils";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {useTriggerFixedRateCreate} from "@/hook";
import * as timers from "timers";
import {useFuncList} from "@/hook/func.ts";

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
    rate: z.number().min(1),
    timeunit: z.string().min(2).max(50),
})

const FormFuncSelect = ({flowId, form}) => {
    const [listFuncs, funcList, loading] = useFuncList()

    // const [funcs, loading] = useApiFuncList(flowId)


    useEffect(() => {
        if (flowId) {
            listFuncs(flowId)
        }
    }, [flowId]);

    if (loading || !form) {
        return "Loading..."
    }

    return (

        <FormField
            control={form.control}
            name="funcId"
            render={({field}) => (
                <FormItem>
                    <FormLabel>Function</FormLabel>
                    <div className="relative w-max">
                        <Select
                            onValueChange={field.onChange}
                        >
                            <FormControl>
                                <SelectTrigger className="w-[280px]">
                                    <SelectValue placeholder="Select a function"/>
                                </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                                <SelectGroup>
                                    {funcList.funcs.map(func =>
                                        <SelectItem key={func.id} value={func.id}> {func.name} </SelectItem>
                                    )}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <FormDescription>
                        The function will be invoked by your trigger
                    </FormDescription>
                    <FormMessage/>
                </FormItem>
            )}
        />
    )
}


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
            rate: 1,
            timeunit: "Minute"
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        console.log(values)

        let timeUnit = 'S'

        switch (values.timeunit) {
            case "Second" : {
                timeUnit = "S";
                break
            }
            case "Minute" : {
                timeUnit = "M";
                break
            }
            case "Hour" : {
                timeUnit = "H";
                break
            }
        }

        try {
            console.log(auth)
            createTrigger(
                flow.id,
                values.funcId,
                values.name,
                "PT" + values.rate + timeUnit
            )
        } catch (e) {
            console.log(`login failed - ${e}`)
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

                            <FormFuncSelect flowId={flow.id} form={form}/>

                            <div className="flex flex-row">
                                <FormField
                                    control={form.control}
                                    name="rate"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormControl>
                                                <Input placeholder={"rate"} {...field} />
                                            </FormControl>

                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="timeunit"
                                    render={({field}) => (
                                        <FormItem>
                                            <div className="relative w-max">
                                                <FormControl>
                                                    <select
                                                        className={cn(
                                                            buttonVariants({variant: "outline"}),
                                                            "w-[200px] appearance-none bg-transparent font-normal"
                                                        )}
                                                        {...field}
                                                    >
                                                        <option value="Second">Seconds</option>
                                                        <option value="Minute">Minutes</option>
                                                        <option value="Hours">Hours</option>
                                                    </select>
                                                </FormControl>
                                                <ChevronDownIcon className="absolute right-3 top-2.5 h-4 w-4 opacity-50"/>
                                            </div>
                                        </FormItem>
                                    )}
                                />
                                <FormMessage/>
                            </div>
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
