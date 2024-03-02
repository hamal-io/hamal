import {NamespaceListItem} from "@/types";

class Node<T> {
    node: T
    descendants: Array<Node<T>>

    constructor(node: T) {
        this.node = node
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
    depth: number

    constructor(node: NamespaceListItem, depth: number = 0) {
        super(node);
        this.depth = depth
    }

    addDescendant(other: Node<NamespaceListItem>) {
        if (this.node.id === other.node.id) {
            return
        }
        super.addDescendant(other);
    }
}