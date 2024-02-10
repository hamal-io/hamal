import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/func-list/components/create.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useFuncList} from "@/hook/func.ts";
import {FuncListItem, Group} from "@/types";
import {string} from "zod";

type  GroupProps = {
    id: string;
    name: string;
}

type ListProps = {
    group: GroupProps
}

const List: FC<ListProps> = ({group}) => {
    const [listFuncs, funcList, loading, error] = useFuncList()

    useEffect(() => {
        const abortController = new AbortController();
        listFuncs(group.id, abortController)
        return () => {
            abortController.abort();
        };
    }, [group]);

    if (error) return `Error`
    if (funcList == null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Functions"
                description={`Functions of your group ${group.name}`}
                actions={[<Create group={group}/>]}
            />
            <Separator className="my-6"/>
            {
                funcList.funcs.length ? (<Content groupId={group.id} funcs={funcList.funcs}/>) : (<NoContent group={group}/>)
            }
        </div>
    );
}

type ContentProps = {
    groupId: string;
    funcs: FuncListItem[]
}

const Content: FC<ContentProps> = ({groupId, funcs}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {funcs.map((func) => (
                <Card
                    key={func.id}
                    className="relative overfunc-hidden duration-500 hover:border-primary/50 group"
                    onClick={() => {
                        navigate(`/groups/${groupId}/functions/${func.id}`)
                    }}
                >
                    <CardHeader>
                        <div className="flex items-center justify-between ">
                            <CardTitle>{func.name}</CardTitle>
                        </div>
                    </CardHeader>
                    <CardContent>
                        <dl className="text-sm leading-6 divide-y divide-gray-100 ">
                            <div className="flex justify-between py-3 gap-x-4">
                            </div>
                        </dl>
                    </CardContent>
                </Card>
            ))}
        </ul>
    )
}

type NoContentProps = {
    group: GroupProps;
}
const NoContent: FC<NoContentProps> = ({group}) => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Funcs found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Funcs yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create group={group}/>
            <GoToDocumentation link={"/funcs"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;