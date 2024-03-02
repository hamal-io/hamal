import {Node} from "@/pages/app/workspace-detail/tab/namespace-list/components/types.ts";
import {NamespaceListItem} from "@/types";
import {useNamespaceList} from "@/hook";
import {useCallback, useEffect, useState} from "react";

type TreeAction = () => void
export function useNamespaceTree(workspaceId: string): [TreeAction, Node<NamespaceListItem>] {
    const [listNamespaces, namespacesList] = useNamespaceList()
    const [root, setRoot] = useState(null)

    const fn = useCallback(() => {
        listNamespaces(workspaceId)
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
    const store = new Array<Node<NamespaceListItem>>
    for (const ns of list.reverse()) {
        const current = new Node(ns)
        store.push(current)
        const parent = store.find(storeItem => storeItem.node.id === ns.parentId)
        if (parent) {
            parent.addDescendant(current)
        }
    }
    return store[0]
}