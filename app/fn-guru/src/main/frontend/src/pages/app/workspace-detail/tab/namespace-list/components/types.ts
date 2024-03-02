export class Node<T> {
    node: T
    descendants: Array<Node<T>>
    depth: number

    constructor(node: T) {
        this.node = node
        this.descendants = []
        this.depth = 0
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