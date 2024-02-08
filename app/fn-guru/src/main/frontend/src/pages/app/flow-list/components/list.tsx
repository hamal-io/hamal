import React, {FC, useEffect} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/namespace-list/components/create.tsx";
import {GoToDocumentation} from "@/components/documentation.tsx";
import {useNavigate} from "react-router-dom";
import {useNamespaceList} from "@/hook/namespace.ts";
import {NamespaceListItem} from "@/types";

type ListProps = {
    groupId: string
}

const List: FC<ListProps> = ({groupId}) => {
    const [listNamespaces, namespaceList, isLoading, error] = useNamespaceList()

    useEffect(() => {
        const abortController = new AbortController()
        listNamespaces(groupId, abortController)
        return () => {
            abortController.abort()
        }
    }, [groupId]);

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    const filteredNamespaces = namespaceList.namespaces.filter(namespace => namespace.name !== '__default__')
    return (
        <div className="pt-8 px-8">
            <PageHeader title="Worknamespaces" description="Organise your worknamespaces" actions={[<Create/>]}/>
            <Separator className="my-6"/>
            {
                filteredNamespaces.length ? (<Content namespaces={filteredNamespaces}/>) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    namespaces: NamespaceListItem[]
}

const Content: FC<ContentProps> = ({namespaces}) => {
    const navigate = useNavigate()
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
            {namespaces.map((namespace) => (
                <Card
                    key={namespace.id}
                    className="relative overnamespace-hidden duration-500 hover:border-primary/50 group"
                    onClick={() => {
                        navigate(`/namespaces/${namespace.id}`)
                    }}
                >
                    <CardHeader>
                        <div className="flex items-center justify-between ">
                            <CardTitle>{namespace.name}</CardTitle>
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

const NoContent: FC = () => (
    <EmptyPlaceholder className="my-4 ">
        <EmptyPlaceholder.Icon>
            {/*<Code />*/}
        </EmptyPlaceholder.Icon>
        <EmptyPlaceholder.Title>No Namespaces found</EmptyPlaceholder.Title>
        <EmptyPlaceholder.Description>
            You haven&apos;t created any Namespaces yet.
        </EmptyPlaceholder.Description>
        <div className="flex flex-col items-center justify-center gap-2 md:flex-row">
            <Create/>
            <GoToDocumentation link={"/namespaces"}/>
        </div>
    </EmptyPlaceholder>
)

export default List;