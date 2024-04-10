import React, {FC, ReactNode, useEffect, useState} from "react";
import {useFlowCreate} from "@/hooks/flow.ts";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Form, FormControl, FormField, FormItem, FormLabel} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Loader2, Plus} from "lucide-react";
import {useNavigate} from "react-router-dom";


const Create = ({}) => {
    const navigate = useNavigate()
    const [open, setOpen] = useState<boolean>(false)
    const [createFlow, createdFlow] = useFlowCreate()
    const [loading, setLoading] = useState<boolean>(false)

    const formSchema = z.object({
        name: z.string()
    })
    type FlowForm = z.infer<typeof formSchema>

    const form = useForm<FlowForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: ""
        }

    })

    const onSubmit = (values: FlowForm) => {
        setLoading(true)
        try {
            const abortController = new AbortController()
            createFlow(values.name, "v1_web3_new_lp_pair", "print('hamal')", abortController)
            return (() => abortController.abort())
        } catch (e) {
            console.error(e)
        }
    }

    useEffect(() => {
        if (createdFlow) {
            setLoading(false)
            setOpen(false)
            navigate(`/flows/${createdFlow.id}`)
        }
    }, [createdFlow]);

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>
                    <Plus className="w-4 h-4 mr-1"/>
                    New Flow
                </Button>
            </DialogTrigger>

            <DialogContent>
                <DialogHeader>Create Flow</DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        <FormField
                            control={form.control}
                            name="name"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Name your Flow</FormLabel>
                                    <FormControl>
                                        <p>
                                            <Input type={"text"} placeholder={"An appropriate name for the magic which is going to happen"} {...field} />

                                        </p>
                                    </FormControl>
                                </FormItem>
                            )}
                        />
                        <Button className="w-full" type={"submit"}>
                            {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                            Create
                        </Button>
                    </form>
                </Form>

            </DialogContent>
        </Dialog>
    )
}

export default Create