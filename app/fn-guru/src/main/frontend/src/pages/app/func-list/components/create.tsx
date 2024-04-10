import {useNavigate} from "react-router-dom";
import React, {FC, useContext, useEffect, useState} from "react";

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
import {useAuth} from "@/hook/auth.ts";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useFuncCreate} from "@/hook/func.ts";
import {useUiState} from "@/hook/ui-state.ts";
import FormHttpMethodSelect from "@/components/form/http-method-select.tsx";
import FormCodeTypeSelect from "@/components/form/code-type-select.tsx";


const formSchema = z.object({
    name: z.string().min(2).max(50),
    codeType: z.string().min(1).max(200)
})

const Create = () => {
    const [uiState] = useUiState()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)

    const [createFunc, submittedFunc] = useFuncCreate()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: "",
            codeType: "",
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        try {
            createFunc(uiState.namespaceId, values.name, values.codeType)
        } catch (e) {
            console.error(e)
        } finally {
            // setLoading(false)
        }

    }

    useEffect(() => {
        if (submittedFunc !== null) {
            navigate(`/functions/${submittedFunc.id}`)
            setOpenDialog(false)
        }
    }, [submittedFunc, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        <Plus className="w-4 h-4 mr-1"/>
                        New Function
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Create function</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Function-One" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            Name of your function
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <FormCodeTypeSelect name='codeType' form={form}/>
                            <Button type="submit">
                                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Create function
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}


export default Create;