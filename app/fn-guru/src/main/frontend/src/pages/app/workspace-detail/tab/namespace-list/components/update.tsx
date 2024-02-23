import React, {FC, useEffect, useState} from "react";

import * as z from "zod"
import {zodResolver} from "@hookform/resolvers/zod";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {useForm} from "react-hook-form";
import {Loader2} from "lucide-react";
import {DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useNamespaceUpdate} from "@/hook";
import {NamespaceListItem} from "@/types";


const formSchema = z.object({
    name: z.string().min(2).max(50),
})

type Props = { item: NamespaceListItem, onClose: () => void }
const Update: FC<Props> = ({item, onClose}) => {
    const [updateNamespace, updateSubmitted] = useNamespaceUpdate()
    const [isLoading, setLoading] = useState(false)

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            name: ""
        },
    })

    async function onSubmit(values: z.infer<typeof formSchema>) {
        setLoading(true)
        try {
            updateNamespace(item.id, values.name)
        } catch (e) {
            console.error(e)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        if (updateSubmitted) {
            onClose()
        }
    }, [updateSubmitted]);


    return (
        <>
            <DialogContent>
                <DialogHeader>Rename Namespace</DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        <FormField
                            control={form.control}
                            name="name"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Name</FormLabel>
                                    <FormControl>
                                        <Input placeholder={item.name} {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <Button type="submit">
                            {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                            Rename
                        </Button>
                    </form>
                </Form>
            </DialogContent>
        </>
    )
}


export default Update;