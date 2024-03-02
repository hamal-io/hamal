import React, {FC, useCallback, useEffect, useState} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import {NamespaceListItem} from "@/types";
import {useNamespaceList} from "@/hook";
import NamespaceNodeView from "@/pages/app/workspace-detail/tab/namespace-list/components/root.tsx";
import {Node} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";


const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [loadNamespaceTree, namespaceTree] = useNamespaceTree()

    console.log("render")

    useEffect(() => {
        loadNamespaceTree(uiState.workspaceId)
    }, []);


    //todo


    function onChange() {
        loadNamespaceTree(uiState.workspaceId)
    }

    if (namespaceTree == null) return "Loading"
    return (
        <div className="pt-8 px-8">
            <NamespaceNodeView node={namespaceTree} changeNode={onChange}/>
        </div>
    )
}

type TreeAction = (workspaceId) => void

function useNamespaceTree(): [TreeAction, Node<NamespaceListItem>] {
    const [listNamespaces, namespacesList, isLoading, error] = useNamespaceList()
    const [root, setRoot] = useState(null)

    function invoke(workspaceId: string): Promise<void> {
        listNamespaces(workspaceId)
        return new Promise((resolve, reject) => {
            const checkLoading = setInterval(() => {
                if (!isLoading) {
                    clearInterval(checkLoading)
                    resolve()
                }
                setTimeout(() => {
                    reject("timeout")
                }, 5000)
            }, 100)


        })
    }

    const fn = useCallback((workspaceId: string) => {
        invoke(workspaceId)
            .then(res => {
                if (namespacesList !== null) {
                    const t = unroll(namespacesList.namespaces, workspaceId)
                    setRoot(t)
                }

            })
            .catch(e => console.log(e))
    }, [namespacesList])

    return [fn, root]
}


function unroll(list: Array<NamespaceListItem>, rootId: string) {
    const store = new Array<Node<NamespaceListItem>>
    for (const ns of list) {
        const current = new Node(ns)
        store.push(current)
        const parent = store.find(storeItem => storeItem.node.id === ns.parentId)
        if (parent != undefined) {
            parent.addDescendant(current)
        }
    }
    return store.find(id => id.node.id === rootId)
}


export default WorkspaceNamespaceListTab;


