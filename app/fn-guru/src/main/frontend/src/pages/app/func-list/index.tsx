import React, {FC, useContext, useEffect} from "react";
import {GroupLayoutContext} from "@/components/app/layout";
import {useFuncList} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/func-list/components/create.tsx";
import {Separator} from "@/components/ui/separator.tsx";
import {FuncListItem} from "@/types";
import {useNavigate} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";

type Props = {}
const FuncListPage: FC<Props> = ({}) => {
    const {groupId, groupName, namespaceId} = useContext(GroupLayoutContext)
    const [listFuncs, funcList, loading, error] = useFuncList()

    useEffect(() => {
        const abortController = new AbortController();
        listFuncs(namespaceId, abortController)
        return () => {
            abortController.abort();
        };
    }, [namespaceId]);

    if (error) return `Error`
    if (funcList == null || loading) return "Loading..."

    return (
        <div className="pt-2 px-2">
            <PageHeader
                title="Functions"
                description={`Functions of your group ${groupName}`}
                actions={[<Create/>]}
            />
            <Separator className="my-6"/>
            {
                funcList.funcs.length ? (<Content
                    groupId={groupId}
                    namespaceId={namespaceId}
                    funcs={funcList.funcs}
                />) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    groupId: string;
    namespaceId: string;
    funcs: FuncListItem[]
}

const Content: FC<ContentProps> = ({groupId, namespaceId, funcs}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-1 xl:grid-cols-3">
            {funcs.map((func) => (
                <Card
                    key={func.id}
                    className="relative overfunc-hidden duration-500 hover:border-primary/50 group"
                    onClick={() => {
                        navigate(`/groups/${groupId}/namespaces/${namespaceId}/functions/${func.id}`)
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


const NoContent = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Funcs found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Funcs yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
            <GoToDocumentation link={"/funcs"}/>
        </div>
    </EmptyPlaceholder>
)

export default FuncListPage