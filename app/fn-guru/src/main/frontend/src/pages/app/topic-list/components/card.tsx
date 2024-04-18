import React, {FC, useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {useTriggerEventCreate} from "@/hook";
import * as z from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Plus} from "lucide-react";
import {Dialog, DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Form} from "@/components/ui/form.tsx";
import FormFuncSelect from "@/components/form/func-select.tsx";
import {TopicListItem} from "@/types/topic.ts";

const formSchema = z.object({
    funcId: z.string().min(1, "Function required"),
})

type TopicCardProps = {
    namespaceId: string,
    topic: TopicListItem;
    // topicWithFuncs: TopicWithFuncs,
    // onChange: () => void
}

const TopicCard: FC<TopicCardProps> = ({namespaceId, topic}) => {
    const navigate = useNavigate()
    const [open, setOpen] = useState(false)
    const [addTrigger, addTriggerResponse, loading, error] = useTriggerEventCreate()

    function handleReq(func) {
        const abortController = new AbortController()
        addTrigger(func, abortController)
        return (() => abortController.abort())
    }

    function handleTriggerCreate(values: z.infer<typeof formSchema>) {
        const {funcId} = values
        try {
            handleReq({
                namespaceId: namespaceId,
                topicId: topic.id,
                funcId: funcId,
                name: funcId + "-" + topic.name
            })
        } catch (e) {
            console.log(e)
        } finally {
            setOpen(false)

        }
    }

/*
    useEffect(() => {
        if (addTriggerResponse) {
            onChange()
        }
    }, [addTriggerResponse]);
*/


    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema)
    })

    if (error) return `Error`
    if (loading) return "Loading..."


    return (
        <>
            <Card
                className="relative overtopic-hidden duration-500 hover:border-primary/50 group"
                onClick={() => navigate(`/topics/${topic.id}`)}
            >
                <CardHeader>
                    <CardTitle>{topic.name}</CardTitle>
                </CardHeader>
                <CardContent>
                    <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                        <div className="flex justify-between py-3 gap-x-4">
                            {topic.type}
                            <Button onClick={(e) => {
                                setOpen(true)
                                e.stopPropagation()
                            }}>
                                <Plus className="w-4 h-4 mr-1"/>
                                Trigger
                            </Button>
                        </div>

                    </dl>
                    {/*<div className="flex flex-col items-start justify-between ">*/}
                    {/*    {topicWithFuncs.funcs.map(func => {*/}
                    {/*        return (*/}
                    {/*            <span className="flex justify-between w-full" key={func.id}>*/}
                    {/*                {func.name}*/}
                    {/*            </span>*/}
                    {/*        )*/}
                    {/*    })}*/}
                    {/*</div>*/}
                </CardContent>
            </Card>
            <Dialog open={open} onOpenChange={setOpen}>
                <DialogContent>
                    <DialogHeader>Add a Trigger to this Topic</DialogHeader>
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(handleTriggerCreate)} className="space-y-8">
                            <FormFuncSelect name={"funcId"} form={form}/>
                            <Button type="submit">
                                Add
                            </Button>
                        </form>
                    </Form>
                </DialogContent>
            </Dialog>
        </>
    )
}

export default TopicCard