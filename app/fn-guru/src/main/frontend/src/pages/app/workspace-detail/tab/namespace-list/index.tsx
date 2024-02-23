import React, {FC, useEffect, useState} from 'react'
import {PageHeader} from "@/components/page-header.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {useNamespaceList} from "@/hook";
import {useUiState} from "@/hook/ui-state.ts";
import NamespaceActions from "@/pages/app/workspace-detail/tab/namespace-list/components/actions.tsx";
import Append from "@/pages/app/workspace-detail/tab/namespace-list/components/append.tsx";
import {Dialog, DialogTrigger} from "@/components/ui/dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Plus} from "lucide-react";


const WorkspaceNamespaceListTab: React.FC = () => {
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

    const _this = namespaceList.namespaces.at(namespaceList.namespaces.length - 1)

    return (
        <div className="pt-8 px-8">
            <PageHeader
                title="Namespaces"
                description=""
                actions={[
                    <CreateNamespace currNamespace={uiState.namespaceId}/>,
                    <NamespaceActions item={_this}/>
                ]}/>
            Current Namespace: {_this.name}
            <ul className="grid grid-cols-1 gap-x-6 gap-y-8">
                {namespaceList.namespaces.map((item) => (
                    <Card
                        className="relative overflow-hidden duration-500 hover:border-primary/50 group"
                    >
                        <CardHeader>
                            <div className="flex items-center justify-between">
                                <CardTitle>{item.name}</CardTitle>
                            </div>
                        </CardHeader>
                        <CardContent>
                            <NamespaceActions item={item}/>
                        </CardContent>
                    </Card>

                ))}
            </ul>
        </div>
    );
}

type CreateProps = { currNamespace: string }
const CreateNamespace: FC<CreateProps> = ({currNamespace}) => {
    const [open, setOpen] = useState(false)

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button>
                    <Plus className="w-4 h-4 mr-1"/>
                    Append
                </Button>
            </DialogTrigger>
            <Append parentId={currNamespace} onClose={() => setOpen(false)}/>
        </Dialog>

    )
}


export default WorkspaceNamespaceListTab;
