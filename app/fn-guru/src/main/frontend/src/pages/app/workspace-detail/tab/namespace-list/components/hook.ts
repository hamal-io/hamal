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
            const t = unroll(namespacesList.namespaces, workspaceId)
            setRoot(t)
        }
    }, [namespacesList]);

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