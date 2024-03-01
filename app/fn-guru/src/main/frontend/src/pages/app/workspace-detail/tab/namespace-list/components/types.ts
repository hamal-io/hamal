export class Node<T> {
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
}