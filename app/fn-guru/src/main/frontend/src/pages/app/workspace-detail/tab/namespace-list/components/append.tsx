import {useNavigate} from "react-router-dom";
import React, {FC, useEffect, useState} from "react";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {Loader2, Plus} from "lucide-react";
import {useAuth} from "@/hook/auth.ts";
import {Dialog, DialogContent, DialogHeader, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useNamespaceAppend} from "@/hook";


const formSchema = z.object({
    name: z.string().min(2).max(50),
})

type Props = {
    parentId: string
    onClose: () => void
}
const Append: FC<Props> = ({parentId, onClose}) => {
    const navigate = useNavigate()
    const [isLoading, setLoading] = useState(false)
    const [appendNamespace, requestedNamespace] = useNamespaceAppend()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: ""
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            appendNamespace(parentId, values.name)
        } catch (e) {
            console.error(e)
        }
    }

    useEffect(() => {
        if (requestedNamespace) {
            onClose()
        }
    }, [requestedNamespace, navigate]);

    return (
        <DialogContent>
            <DialogHeader>Create Namespace</DialogHeader>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                    <FormField
                        control={form.control}
                        name="name"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Name</FormLabel>
                                <FormControl>
                                    <Input placeholder="Worknamespace-One" {...field} />
                                </FormControl>
                                <FormDescription>
                                    Name of your namespace
                                </FormDescription>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <Button type="submit">
                        {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                        Create namespace
                    </Button>
                </form>
            </Form>
        </DialogContent>
    )
}


export default Append;