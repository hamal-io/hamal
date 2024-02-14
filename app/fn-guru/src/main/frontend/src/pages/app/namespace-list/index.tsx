import React, {FC, useContext, useEffect} from 'react'
import {PageHeader} from "@/components/page-header.tsx";
import Create from "@/pages/app/namespace-list/components/create.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {NamespaceListItem} from "@/types";
import {useNamespaceList} from "@/hook";
import {useUiState} from "@/hook/ui-state.ts";

const NamespaceListPage: React.FC = () => {
    const [uiState] = useUiState()
    const [listNamespaces, namespaceList, isLoading, error] = useNamespaceList()

    useEffect(() => {
        const abortController = new AbortController()
        listNamespaces(uiState.workspaceId, abortController)
        return () => {
            abortController.abort()
        }
    }, [uiState.workspaceId]);

    if (namespaceList == null || isLoading) return "Loading..."
    if (error != null) return "Error -"

    return (
        <div className="pt-8 px-8">
            <PageHeader title="Namespaces" description="Organise your workflows" actions={[<Create/>]}/>
            <Content
                namespaces={namespaceList.namespaces}
            />
        </div>
    );
}

type ContentProps = {
    namespaces: NamespaceListItem[]
}

const Content: FC<ContentProps> = ({namespaces}) => {
    return (
        <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
            {namespaces.map((namespace) => (
                <Card
                    key={namespace.id}
                    className="relative overflow-hidden duration-500 hover:border-primary/50 group"
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


export default NamespaceListPage;
