import React, {FC, useEffect, useState} from "react";
import {PageHeader} from "@/components/page-header.tsx";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import Create from "@/pages/app/topic-list/components/create.tsx";
import {useUiState} from "@/hook/ui-state.ts";
import {Button} from "@/components/ui/button.tsx";
import {Plus} from "lucide-react";
import {Dialog, DialogContent, DialogHeader} from "@/components/ui/dialog.tsx";
import {Form, FormControl, FormDescription, FormField, FormItem, FormMessage} from "@/components/ui/form.tsx";
import * as z from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useFuncList, useTriggerEventCreate} from "@/hook";
import {FuncListItem} from "@/types";
import {TopicWithFuncs, useTopicsWithFuncs} from "@/pages/app/topic-list/components/hook.ts";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";


const TopicListPage = () => {
    const [uiState] = useUiState()
    const [getTopicsWithFuncs, topicWithFuncs, loading, error] = useTopicsWithFuncs()

    const doUpdate = () => getTopicsWithFuncs(uiState.namespaceId)

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

    if (loading) return "Loading..."
    if (error) return "Error"

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Topics"
                description={`Topics TBD`}
                actions={[
                    <Create onClose={doUpdate}/>
                ]}
            />
            {topicWithFuncs.length !== 0 ?
                <ul className="grid grid-cols-3 gap-4">
                    {topicWithFuncs.map(topic =>
                        <li key={topic.topic.id}>
                            <TopicCard namespaceId={uiState.namespaceId} topicWithFuncs={topic}/>
                        </li>
                    )}
                </ul> : Empty
            }


        </div>)
}

type TopicCardProps = { namespaceId: string, topicWithFuncs: TopicWithFuncs }
const TopicCard: FC<TopicCardProps> = ({namespaceId, topicWithFuncs}) => {
    const navigate = useNavigate()
    const [open, setOpen] = useState(false)
    const [addTrigger, AddTriggerResponse, loading, error] = useTriggerEventCreate()

    function handleTriggerCreate(func: FuncListItem) {
        try {
            addTrigger({
                namespaceId: namespaceId,
                topicId: topicWithFuncs.topic.id,
                funcId: func.id,
                name: func.name
            })
        } catch (e) {
            console.log(e)
        } finally {
            setOpen(false)
        }
    }

    if (error) return `Error`
    if (loading) return "Loading..."

    return (
        <>
            <Card
                className="relative overtopic-hidden duration-500 hover:border-primary/50 group"
                onClick={() => navigate(`/topics/${topicWithFuncs.topic.id}`)}
            >
                <CardHeader>
                    <CardTitle>{topicWithFuncs.topic.name}</CardTitle>
                </CardHeader>
                <CardContent>
                    <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                        <div className="flex justify-between py-3 gap-x-4">
                            {topicWithFuncs.topic.type}
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
                        {topicWithFuncs.funcs.map(func => {
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

type TriggerDialogProps = { submit: (func: FuncListItem) => void }
const TriggerDialog: FC<TriggerDialogProps> = ({submit}) => {
    const formSchema = z.object({
        func: z.object({
            id: z.string(),
            name: z.string()
        })
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            func: {
                id: "",
                name: ""
            }
        }
    })

    function onSubmit(values: z.infer<typeof formSchema>) {
        const {id, name} = values.func
        submit({id, name})
    }

    return (
        <DialogContent>
            <DialogHeader>Add a Trigger to this Topic</DialogHeader>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                    <FuncSelect form={form}/>
                    <Button type="submit" className={"float-right"}>
                        Select
                    </Button>
                </form>
            </Form>
        </DialogContent>
    )
}

const FuncSelect = ({form}) => {
    const [uiState] = useUiState()
    const [listFuncs, funcList, loading] = useFuncList()

    useEffect(() => {
        listFuncs(uiState.namespaceId)
    }, [uiState.namespaceId]);


    if (funcList == null || loading || !form) {
        return "Loading..."
    }

    return (
        <FormField
            control={form.control}
            name="func"
            render={({field}) => (
                <FormItem>
                    <div className="relative w-max">
                        <Select
                            onValueChange={value => {
                                const func = funcList.funcs.find(f => f.id === value)
                                if (func) {
                                    field.onChange({id: func.id, name: func.name})
                                }
                            }}
                        ><FormControl>
                                <SelectTrigger className="w-[280px]">
                                    <SelectValue placeholder="Select a function"/>
                                </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                                <SelectGroup>
                                    {funcList.funcs.map(func =>
                                        <SelectItem key={func.id} value={func.id}> {func.name} </SelectItem>
                                    )}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </div>
                    <FormDescription>
                        The function will be invoked by your trigger
                    </FormDescription>
                    <FormMessage/>
                </FormItem>
            )}
        />
    )
}

export default TopicListPage