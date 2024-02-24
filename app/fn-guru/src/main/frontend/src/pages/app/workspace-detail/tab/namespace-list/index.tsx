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
    const [currentNamespace, setCurrentNamespace] = useState<string>(uiState.workspaceId)
    const [children, setChildren] = useState<NamespaceList>()

    useEffect(() => {
        const abortController = new AbortController()
        getSublist(currentNamespace, abortController)
        return () => {
            abortController.abort()
        }
    }, [currentNamespace]);


    useEffect(() => {
        if (sublist) {
            const c = findChildren(currentNamespace, sublist)
            if (c.namespaces.length === 0) {
                setChildren(sublist)
            }
            setChildren(c)

        }
    }, [sublist]);

    if (currentNamespace == null || isLoading) return "Loading..."
    if (sublist == null || isLoading) return "Loading..."
    if (error != null) return "Error -"


    function onChange(id: string) {
        setCurrentNamespace(id)
    }

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                description=""
                actions={[
                    <Append appendTo={currentNamespace}/>,
                    // <Actions item={root}/>
                ]}/>
            Current Position: {sublist.namespaces[0].name}
            <ListView sublist={sublist} onChange={onChange}/>
        </div>
    )
}


type ListViewProps = { sublist: NamespaceList, onChange: (id: string) => void }
const ListView: FC<ListViewProps> = ({sublist, onChange}) => {
    const list = sublist.namespaces.map(namespace =>
        <li className="grid grid-cols-1 gap-x-6 gap-y-8" key={namespace.id}>
            <Card
                className="relative overflow-hidden duration-500 hover:border-primary/50 group"
                key={namespace.id}
                onClick={() => {
                    onChange(namespace.id)
                }}
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
        </li>
    )

    return (
        <ul>
            {list}
        </ul>
    )
}


const findChildren = (parentId: string, list: NamespaceList): NamespaceList => {
    const items = list.namespaces.filter(item => item.parentId === parentId)
    return {namespaces: items}
}

export default WorkspaceNamespaceListTab;
