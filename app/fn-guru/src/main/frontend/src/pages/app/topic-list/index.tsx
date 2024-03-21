import React, {FC, useCallback, useEffect, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {TopicListItem} from "@/types/topic.ts";
import Create from "@/pages/app/topic-list/components/create.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {Button} from "@/components/ui/button.tsx";
import {Plus} from "lucide-react";
import {Dialog, DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Form} from "@/components/ui/form.tsx";
import FormFuncSelect from "@/components/form/func-select.tsx";
import * as z from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useTopicList} from "@/hook/topic.ts";
import {useTriggerEventCreate, useTriggerListEvent} from "@/hook";
import {FuncListItem} from "@/types";
import {Simulate} from "react-dom/test-utils";


type TopicWithFuncs = {
    topic: TopicListItem
    funcs: Array<FuncListItem>
}
type UpdateAction = (namespaceId: string, abortController?: AbortController) => void
const useTopicsWithFuncs = (): [UpdateAction, Array<TopicWithFuncs>, boolean, Error] => {
    const [listTriggers, triggerList, triggerLoading, triggersError] = useTriggerListEvent()
    const [listTopics, topicList, topicsLoading, topicsError] = useTopicList()
    const [topicsWithFuncs, setTopicWithFuncs] = useState<Array<TopicWithFuncs>>(null)

    const fn = useCallback<UpdateAction>(async (namespaceId, abortController?) => {
        listTopics(namespaceId)
        listTriggers(namespaceId)
    }, [])


    useEffect(() => {
        if (topicList && triggerList) {
            const x: Array<TopicWithFuncs> = topicList.topics.map(topic => {
                return {
                    topic: topic,
                    funcs: triggerList.triggers.filter(trigger => trigger.topic.id === topic.id)
                }
            })
            setTopicWithFuncs(x)
        }
    }, [topicList, triggerList]);

    const error = topicsError || triggersError
    const loading = topicsLoading || triggerLoading
    return [fn, topicsWithFuncs, loading, error]
}

type Props = {}
const TopicListPage: FC<Props> = ({}) => {
    const [uiState] = useUiState()
    const [listTopics, topicList, topicsLoading, topicsError] = useTopicList()
    const [updateTopics, topics, updateLoading, updateError] = useTopicsWithFuncs()

    const doUpdate = () => updateTopics(uiState.namespaceId)

    useEffect(() => {
        doUpdate()
    }, []);


    const Empty = (
        <EmptyPlaceholder className="my-4 ">
            <EmptyPlaceholder.Icon>
                {/*<Code />*/}
            </EmptyPlaceholder.Icon>
            <EmptyPlaceholder.Title>No Topics found</EmptyPlaceholder.Title>
            <EmptyPlaceholder.Description>
                You haven&apos;t created any Topics yet.
            </EmptyPlaceholder.Description>
            <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
                <Create onClose={doUpdate}/>
                <GoToDocumentation link={"/topics"}/>
            </div>
        </EmptyPlaceholder>)

    if (topicsError || updateError) return "Error"
    if (topicsLoading || updateLoading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Topics"
                description={`Topics TBD`}
                actions={[
                    <Create onClose={doUpdate}/>
                ]}
            />
            {topicList.topics.length !== 0 ?
                <ul className="grid grid-cols-3 gap-4">
                    {topics.map(topic =>
                        <li key={topic.topic.id}>
                            <TopicCard namespaceId={uiState.namespaceId} topic={topic}/>
                        </li>
                    )}
                </ul> : Empty
            }


        </div>)
}

type TopicCardProps = { namespaceId: string, topic: TopicListItem }
const TopicCard: FC<TopicCardProps> = ({namespaceId, topic}) => {
    const navigate = useNavigate()
    const [open, setOpen] = useState(false)
    const [addTrigger, AddTriggerResponse, loading, error] = useTriggerEventCreate()
    const [listTriggers, triggerList, triggerLoading, triggersError] = useTriggerListEvent()

    function handleTriggerCreate(func: FuncListItem) {
        try {
            addTrigger({
                namespaceId: namespaceId,
                topicId: topic.id,
                funcId: func.id,
                name: func.name
            })
        } catch (e) {
            console.log(e)
        } finally {
            setOpen(false)
        }
    }

    useEffect(() => {
        listTriggers(namespaceId)
    }, []);

    useEffect(() => {

    }, [triggerList]);

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
                    <div className="flex flex-col items-start justify-between ">
                        {topic.funcs.map(func => {
                            return (
                                <span className="flex justify-between w-full" key={func.id}>
                                    {func.name}
                                </span>
                            )
                        })}
                    </div>
                </CardContent>
            </Card>
            <Dialog open={open} onOpenChange={setOpen}>
                <TriggerDialog submit={handleTriggerCreate}/>
            </Dialog>
        </>
    )
}

type TriggerDialogProps = { submit: (funcId: FuncListItem) => void }
const TriggerDialog: FC<TriggerDialogProps> = ({submit}) => {
    const formSchema = z.object({
        funcId: z.string().min(1, "Function required"),
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            funcId: ""
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        submit({id: values.funcId, name: "null"}) //TODO-273 Selector retrieve name too
    }

    return (
        <DialogContent>
            <DialogHeader></DialogHeader>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                    <FormFuncSelect name='funcId' form={form}/>
                    <Button type="submit" className={"float-right"}>
                        {/*{loading && <Loader2 className="mr-2 h-4 w-4 animate-spin"/>}*/}
                        Select
                    </Button>
                </form>
            </Form>
        </DialogContent>
    )
}

export default TopicListPage