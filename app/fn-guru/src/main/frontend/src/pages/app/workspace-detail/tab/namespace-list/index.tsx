import React, {FC, useEffect, useState} from 'react'
import {useNamespaceSublist} from "@/hook";
import {useUiState} from "@/hook/ui-state.ts";
import {PageHeader} from "@/components/page-header.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";

import {NamespaceList} from "@/types";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import Actions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";


const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [getSublist, sublist, isLoading, error] = useNamespaceSublist()
    const [currentParent, setCurrentParent] = useState<string>(uiState.workspaceId)
    const [children, setChildren] = useState<NamespaceList>(null)


    useEffect(() => {
        const abortController = new AbortController()
        getSublist(currentParent, abortController)
        return () => {
            abortController.abort()
        }
    }, [currentParent]);


    useEffect(() => {
        if (sublist != null && currentParent != null) {
            if (sublist.namespaces.length == 1) {
                setChildren(sublist)
            }
            const res = sublist.namespaces.filter(item => item.parentId === currentParent)
            setChildren({namespaces: res})
        }
    }, [sublist, currentParent]);



    if (sublist == null || isLoading) return "Loading..."
    if (error != null) return "Error -"


    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                description=""
                actions={[
                    <Append appendTo={currentParent}/>,
                    // <Actions item={root}/>
                ]}/>
            Position: {sublist.namespaces[0].name}
            {children &&
                <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
                    {children.namespaces.map(namespace =>
                        <Card
                            className="relative overflow-hidden duration-500 hover:border-primary/50 group"
                            key={namespace.id}
                            onClick={() => setCurrentParent(namespace.id)}
                        >
                            <CardHeader>
                                <div className="flex items-center justify-between">
                                    <CardTitle>{namespace.name}</CardTitle>
                                </div>
                            </CardHeader>
                            <CardContent>
                                <Actions item={namespace}/>
                            </CardContent>
                        </Card>
                    )}
                </ul>
            }
        </div>
    )
}

/*
type ListViewProps = { sublist: NamespaceList, onChange: (id: string) => void }
const ListView: FC<ListViewProps> = ({sublist, onChange}) => {
    return (

    )
}
*/

export default WorkspaceNamespaceListTab;
