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
import {useTopicGroupCreate} from "@/hook/topic.ts";
import {GroupLayoutContext} from "@/components/app/layout";


const formSchema = z.object({
    name: z.string().min(2).max(50),
})

const Create = () => {
    const {groupId} = useContext(GroupLayoutContext)

    const [auth, setAuth] = useAuth()
    const navigate = useNavigate()
    const [openDialog, setOpenDialog] = useState<boolean>(false)
    const props = {openModal: openDialog, setOpenModal: setOpenDialog}
    const [isLoading, setLoading] = useState(false)

    const [createGroupTopic, groupTopicRequested] = useTopicGroupCreate()

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
        try {
            createGroupTopic(groupId, values.name)
        } catch (e) {
            console.error(e)
        } finally {
            // setLoading(false)
        }

    }

    useEffect(() => {
        if (groupTopicRequested !== null) {
            // navigate(`/groups/${groupId}/topics/${groupTopicRequested.topicId}`)
            setOpenDialog(false)
        }
    }, [groupTopicRequested, navigate]);

    return (
        <>
            <Dialog open={openDialog} onOpenChange={setOpenDialog}>
                <DialogTrigger asChild>
                    <Button>
                        <Plus className="w-4 h-4 mr-1"/>
                        New Topic
                    </Button>
                </DialogTrigger>

                <DialogContent>
                    <DialogHeader>Create topiction</DialogHeader>

                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                            <FormField
                                control={form.control}
                                name="name"
                                render={({field}) => (
                                    <FormItem>
                                        <FormLabel>Name</FormLabel>
                                        <FormControl>
                                            <Input placeholder="Topiction-One" {...field} />
                                        </FormControl>
                                        <FormDescription>
                                            Name of your topic
                                        </FormDescription>
                                        <FormMessage/>
                                    </FormItem>
                                )}
                            />
                            <Button type="submit">
                                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}
                                Create topic
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}


export default Create;