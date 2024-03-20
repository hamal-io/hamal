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
import error = Simulate.error;


type TopicWithFuncs = {
    topic: TopicListItem
    funcs: Array<FuncListItem>
}
type UpdateAction = (namespaceId: string, abortController?: AbortController) => void
const useTopicsWithTriggers = (): [UpdateAction, Array<TopicWithFuncs>, boolean, Error] => {
    const [listTopics, topicList, topicsLoading, topicsError] = useTopicList()
    const [listTriggers, triggerList, triggerLoading, triggersError] = useTriggerListEvent()
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

    const loading = topicsLoading || triggerLoading
    const error = topicsError || triggersError

    return [fn, topicsWithFuncs, loading, error]
}

type Props = {}
const TopicListPage: FC<Props> = ({}) => {
    const [uiState] = useUiState()
    const [updateTopics, topics, error, loading] = useTopicsWithTriggers()

    const doUpdate = () => {
        updateTopics(uiState.namespaceId)
    }

    useEffect(() => {
        doUpdate()
    }, []);

    if (error) return `Error`
    if (loading) return "Loading..."


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


    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Topics"
                description={`Topics TBD`}
                actions={[
                    <Create onClose={doUpdate}/>
                ]}
            />

            <ul className="grid grid-cols-3 gap-4">
                {topics.map(topic =>
                    <li key={topic.topic.id}>
                        <TopicCard namespaceId={uiState.namespaceId} topic={topic}/>
                    </li>
                )}
            </ul>

        </div>)
}

type TopicCardProps = { namespaceId: string, topic: TopicWithFuncs }
const TopicCard: FC<TopicCardProps> = ({topic}) => {
    const navigate = useNavigate()
    const [open, setOpen] = useState(false)
    const [addTrigger, AddTriggerResponse] = useTriggerEventCreate()


    function buttonClick(event: React.MouseEvent<HTMLButtonElement, MouseEvent>) {
        setOpen(true)
        event.stopPropagation()
    }

    const doUpdate = (funcId: string) => {
        const abortController = new AbortController()
        addTrigger({
            namespaceId: namespaceId,
            topicId: topic.id,
            funcId: funcId,
            name: "hi"
        }, abortController)
        return (() => abortController.abort())
    }

    function handleTriggerCreate(funcId: string) {
        try {
            addTrigger({
                namespaceId: namespaceId,
                topicId: topic.id,
                funcId: funcId,
                name: "hi"
            })

        } catch (e) {
            console.log(e)
        } finally {
            setOpen(false)
        }
    }

    useEffect(() => {

    }, []);


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
                            <Button onClick={buttonClick}>
                                <Plus className="w-4 h-4 mr-1"/>
                                Trigger
                            </Button>
                        </div>

                    </dl>
                </CardContent>
            </Card>
            <Dialog open={open} onOpenChange={setOpen}>
                <TriggerDialog submit={handleTriggerCreate}/>
            </Dialog>
        </>
    )
}

type TriggerDialogProps = { submit: (funcId: string) => void }
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
        submit(values.funcId)
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