import React, {FC, useEffect, useState} from "react";
import {useNamespaceSublist} from "@/hook";
import {PageHeader} from "@/components/page-header.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";

type Props = { parent: string }
const SublistView: FC<Props> = ({parent}) => {
    const [getSublist, sublist, isLoading, error] = useNamespaceSublist()
    const [currentRoot, setCurrentRoot] = useState(parent)

    useEffect(() => {
        const abortController = new AbortController()
        getSublist(currentRoot, abortController)
        return () => {
            abortController.abort()
        }
    }, [currentRoot]);

    if (sublist == null || isLoading) return "Loading..."
    if (error != null) return "Error"

    const handleClick = (id: string) => {
        setCurrentRoot(id)
    }

    const root = sublist.namespaces[0]

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                description=""
                actions={[
                    <Append appendTo={root.id}/>,
                    <Actions item={root}/>
                ]}/>
            Current Position: {root.name}
            <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
                {sublist.namespaces.map((item) => (
                    <Card
                        className="relative overflow-hidden duration-500 hover:border-primary/50 group"
                        key={item.id}
                        onClick={() => handleClick(item.id)}
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