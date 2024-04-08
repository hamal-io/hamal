import React, {FC, ReactNode, useEffect, useState} from "react";
import {useFlowCreate} from "@/hooks/flow.ts";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Form, FormControl, FormField, FormItem, FormLabel} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Loader2} from "lucide-react";

type Props = { trigger: ReactNode, onCreate: (FlowCreateRequested) => void }
const Create: FC<Props> = ({trigger, onCreate}) => {
    const [open, setOpen] = useState<boolean>(false)
    const [createFlow, flowCreateRequested] = useFlowCreate()
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

    function onSubmit(values: FlowForm) {
        setLoading(true)
        try {
            const abortController = new AbortController()
            createFlow(values.name, "print('hamal')", abortController)
            return (() => abortController.abort())
        } catch (e) {
            console.error(e)
        }
    }

    useEffect(() => {
        if (flowCreateRequested) {
            onCreate(flowCreateRequested)
            setLoading(false)
            setOpen(false)

        }
    }, [flowCreateRequested]);

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                {trigger}
            </DialogTrigger>
            <DialogContent>
                <DialogHeader title={"Create Flow"}>
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
                                                <Input type={"text"} placeholder={"Sample Flow"} {...field} />

                                            </p>
                                        </FormControl>
                                    </FormItem>
                                )}
                            />
                            <Button type={"submit"}>
                                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Submit
                            </Button>
                        </form>
                    </Form>
                </DialogHeader>
            </DialogContent>
        </Dialog>
    )
}

export default Create