import {NamespaceListItem, NamespaceNode} from "@/types";
import {useNamespaceList} from "@/hook";
import {useCallback, useEffect, useState} from "react";

type TreeAction = (abortcontroller?: AbortController) => void
export function useNamespaceTree(workspaceId: string): [TreeAction, NamespaceNode] {
    const [listNamespaces, namespacesList] = useNamespaceList()
    const [root, setRoot] = useState(null)

    const fn = useCallback<TreeAction>((abortController?) => {
        listNamespaces(workspaceId, abortController)
    }, [])

    useEffect(() => {
        if (namespacesList != null) {
            const t = unroll(namespacesList.namespaces)
            setRoot(t)
        }
    }, [namespacesList]);

    return [fn, root]
}

function unroll(list: Array<NamespaceListItem>) {
    const store = new Array<NamespaceNode>
    for (const ns of list.reverse()) {
        const current = new NamespaceNode(ns)
        store.push(current)
        const parent = store.find(storeItem => storeItem.data.id === ns.parentId)
        if (parent) {
            parent.addDescendant(current)
        }
    }
    return store[0]
}