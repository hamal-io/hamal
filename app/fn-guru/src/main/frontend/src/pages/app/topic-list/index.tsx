import React, {FC, useEffect, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useTopicEventAppend, useTopicList} from "@/hook/topic.ts";
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

type Props = {}
const TopicListPage: FC<Props> = ({}) => {
    const [uiState] = useUiState()
    const [listTopics, topicList, loading, error] = useTopicList()

    useEffect(() => {
        const abortController = new AbortController();
        listTopics(uiState.namespaceId, abortController)
        return () => {
            abortController.abort();
        };
    }, [uiState.namespaceId]);

    if (error) return `Error`
    if (topicList == null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Topics"
                description={`Topics TBD`}
                actions={[<Create/>]}
            />
            {topicList.topics.length !== 0 ?
                <ul className="grid grid-cols-2 gap-4">
                    {topicList.topics.map(item =>
                        <li key={item.id}>
                            <TopicCard topic={item}/>
                        </li>
                    )}
                </ul> : <NoContent/>}
        </div>)
}

type ContentProps = {
    topic: TopicListItem
}
const TopicCard: FC<ContentProps> = ({topic}) => {
    const navigate = useNavigate()
    const [appendEvent, appendRequested, loading, error] = useTopicEventAppend()
    const [open, setOpen] = useState(false)

    function buttonClick(event: React.MouseEvent<HTMLButtonElement, MouseEvent>) {
        setOpen(true)
        event.stopPropagation()
    }

    function append(funcId: string) {
        try {
            appendEvent(topic.id, {funcId: funcId})
        } catch (e) {
            console.log(e)
        } finally {
            setOpen(false)
        }
    }

    function cardClick() {
        console.log("Card click")
        // navigate(`/v1/topics/${topic.id}`)
    }

    return (
        <>
            <Card
                className="relative overtopic-hidden duration-500 hover:border-primary/50 group cursor-pointer"
                onClick={cardClick}
            >
                <CardHeader>
                    <CardTitle>{topic.name}</CardTitle>
                </CardHeader>
                <CardContent>
                    <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                        <div className="flex justify-between py-3 gap-x-4">
                            {topic.type}
                            <Button onClick={() => setOpen(true)}>
                                <Plus className="w-4 h-4 mr-1"/>
                                Trigger
                            </Button>
                        </div>
                    </dl>
                </CardContent>
            </Card>
            <Dialog open={open} onOpenChange={setOpen}>
                <TriggerDialog submit={append}/>
            </Dialog>
        </>
    )
}

type DialogProps = { submit: (funcId: string) => void }
const TriggerDialog: FC<DialogProps> = ({submit}) => {
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
            <DialogHeader>Trigger for this Topic</DialogHeader>
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


const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Topics found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Topics yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
            <GoToDocumentation link={"/topics"}/>
        </div>
    </EmptyPlaceholder>
)

export default TopicListPage