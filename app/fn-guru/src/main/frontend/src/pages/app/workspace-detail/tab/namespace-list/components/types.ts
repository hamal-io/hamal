import {NamespaceListItem} from "@/types";

class Node<T> {
    data: T
    descendants: Array<Node<T>>

    constructor(node: T) {
        this.data = node
        this.descendants = []
    }

    addDescendant(other: Node<T>) {
        if (!this.descendants.includes(other)) {
            this.descendants.push(other)
        }
    }

    isParent() {
        return this.descendants.length > 0
    }
}

export class NamespaceNode extends Node<NamespaceListItem> {

    constructor(node: NamespaceListItem) {
        super(node);
    }

    addDescendant(other: Node<NamespaceListItem>) {
        if (this.data.id === other.data.id) {
            return
        }
        super.addDescendant(other);
    }
}