import React, {FC, useState} from "react";
import {Separator} from "@/components/ui/separator.tsx";
import {EmptyPlaceholder} from "@/components/empty-placeholder.tsx";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {BookOpen, Link, Search} from "lucide-react";
import {PageHeader} from "@/components/page-hader.tsx";
import Create from "@/pages/app/namespace-list/components/create.tsx";
import {Button} from "@/components/ui/button.tsx";

import {useApiNamespaceList} from "@/hook/api/namespace.ts";
import {ApiNamespaceSimple} from "@/api/types";
import {GoToDocumentation} from "@/components/documentation.tsx";

type ListProps = {
    groupId: string
}

const List: FC<ListProps> = (props: ListProps) => {
    const [namespaces, isLoading, error] = useApiNamespaceList(props.groupId)

    if (isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-8 px-8">
            <PageHeader title="Namespaces" description="Organise your workflows" actions={[<Create/>]}/>
            <Separator className="my-6"/>
            {
                namespaces.length ? (<Content namespaces={namespaces}/>) : (<NoContent/>)
            }
        </div>
    );
}

type ContentProps = {
    namespaces: ApiNamespaceSimple[]
}

const Content: FC<ContentProps> = ({namespaces}) => (
    <ul className="grid grid-cols-1 gap-x-6 gap-y-8 lg:grid-cols-2 xl:grid-cols-3">
        {namespaces.map((namespace) => (
            <Card key={namespace.id} className="relative overflow-hidden duration-500 hover:border-primary/50 group ">
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