import {useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {useApiAccountLogin, useFlowCreate, useApiPost} from "@/hook";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {BookOpen, Loader2, Plus, PlusCircle} from "lucide-react";
import {useAuth} from "@/hook/auth.ts";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";


const formSchema = z.object({
    name: z.string().min(2).max(50),
})

const Create = () => {
    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)

    const [createFlow, submittedFlow] = useFlowCreate()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: ""
        },
    })

    // 2. Define a submit handler.
    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        // Do something with the form values.
        // ✅ This will be type-safe and validated.
        console.log(values)

        try {
            createFlow(auth.groupId, values.name)
            console.log(auth)
        } catch (e) {
            console.log(`login failed - ${e}`)
        } finally {
            // setLoading(false)
        }

    }

    useEffect(() => {
        if (submittedFlow !== null) {
            navigate(`/flows/${submittedFlow.flowId}`)
            setOpenDialog(false)

        }
    }, [submittedFlow, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        <Plus className="w-4 h-4 mr-1"/>
                        New Flow
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Create flow</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Workflow-One" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            This is the name of your flow.
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit">
                                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Create Flow
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}


export default Create;