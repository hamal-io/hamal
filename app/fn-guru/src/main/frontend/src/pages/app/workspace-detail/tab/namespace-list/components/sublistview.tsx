import React, {FC, useEffect} from "react";
import {useNamespaceSublist} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";
import {NamespaceListItem} from "@/types";

type Props = {
    parent: NamespaceListItem
}
const SublistView: FC<Props> = ({parent}) => {
    const [getSublist, sublist, isLoading, error] = useNamespaceSublist()

    useEffect(() => {
        const abortController = new AbortController()
        getSublist(parent.id, abortController)
        return () => {
            abortController.abort()
        }
    }, [parent]);

    if (sublist == null || isLoading) return "Loading..."
    if (error != null) return "Error"

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                description=""
                actions={[
                    <Append appendTo={parent.id}/>,
                    <Actions item={parent}/>
                ]}/>
            Current Position: {parent.name}
            <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
                {sublist.namespaces.map((item) => (
                    <Card
                        className="relative overflow-hidden duration-500 hover:border-primary/50 group"
                        key={item.id}
                    >
                        <CardHeader>
                            <div className="flex items-center justify-between">
                                <CardTitle>{item.name}</CardTitle>
                            </div>
                        </CardHeader>
                        <CardContent>
                            <Actions item={item}/>
                        </CardContent>
                    </Card>

                ))}
            </ul>
        </div>
    )
}

export default SublistView