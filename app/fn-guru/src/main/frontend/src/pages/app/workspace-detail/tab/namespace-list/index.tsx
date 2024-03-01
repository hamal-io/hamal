import React, {FC, useCallback, useEffect, useState} from 'react'
import {useUiState} from "@/hook/ui-state.ts";
import {NamespaceListItem} from "@/types";
import {useNamespaceList} from "@/hook";
import NamespaceNodeView from "@/pages/app/workspace-detail/tab/namespace-list/components/root.tsx";
import {Node} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";


const WorkspaceNamespaceListTab: FC = () => {
    const [uiState] = useUiState()
    const [loadTree, tree, isLoading] = useNamespaceTree()

    useEffect(() => {
        loadTree(uiState.workspaceId)
    }, [uiState]);

    function onChange() {
        loadTree(uiState.workspaceId)
    }

    return (
        <div className="pt-8 px-8">
            {isLoading ? "Loading" : <NamespaceNodeView node={tree} changeNode={onChange}/>}
        </div>
    )
}

type NamespaceLoadAction = (workspaceId: string) => void
const useNamespaceTree = (): [NamespaceLoadAction, Node<NamespaceListItem>, boolean] => {
    const [listNamespaces, namespacesList, isLoading, error] = useNamespaceList()
    const [loading, setLoading] = useState(true)
    const [root, setRoot] = useState(null)

    function asyncFetch(workspaceId: string): Promise<Node<NamespaceListItem>> {
        return new Promise((resolve, reject) => {
            const abortController = new AbortController()
            listNamespaces(workspaceId, abortController)
            if (error) reject(error)

            setTimeout(() => {
                if (namespacesList) {
                    console.log("Promise resolve")
                    resolve(unroll(namespacesList.namespaces, workspaceId))
                }
                reject("timeout")
            }, 1000)

            abortController.abort()
        })
    }

    const fn = useCallback(async (workspaceId: string) => {
        asyncFetch(workspaceId)
            .then(res => {
                setLoading(false)
            })
            .catch(e => console.log(e))
    }, [])

    return [fn, root, loading]
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


