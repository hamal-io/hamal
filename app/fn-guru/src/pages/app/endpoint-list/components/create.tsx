import {useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";

import * as z from "zod"
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
import {useForm} from "react-hook-form";
import {Loader2, Plus,} from "lucide-react";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useTriggerEndpointCreate} from "@/hook/trigger.ts";
import FormFuncSelect from "@/components/form/func-select.tsx";
import {useUiState} from "@/hook/ui-state.ts";


const formSchema = z.object({
    name: z.string().min(2).max(50),
    funcId: z.string().min(1, "Function required"),
})

const Create = () => {
    const [uiState] = useUiState()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const [isLoading, setLoading] = useState(false)

    const [createEndpoint, submittedEndpoint] = useTriggerEndpointCreate()
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            funcId: null
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            createEndpoint(uiState.namespaceId, values.funcId, values.name)
        } catch (e) {
            console.error(e)
        } finally { /* empty */
        }

    }

    useEffect(() => {
        if (submittedEndpoint !== null) {
            setOpenDialog(false)
            window.location.reload()
        }
    }, [submittedEndpoint, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        <Plus className="w-4 h-4 mr-1"/>
                        New Endpoint
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Create a new endpoint</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Endpoint-One" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            Name of your endpoint
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />

                            <FormFuncSelect name='funcId' form={form}/>

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