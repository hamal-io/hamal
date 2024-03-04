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

export class NamespaceNode {
    data: NamespaceListItem
    descendants: Array<NamespaceNode>

    constructor(value: NamespaceListItem) {
        this.data = value
        this.descendants = []
    }

    addDescendant(other: Node<NamespaceListItem>) {
        if (this.data.id === other.data.id) {
            return
        }
        this.descendants.push(other)
    }

    isParent() {
        return this.descendants.length > 0
    }
}