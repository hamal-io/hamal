package io.hamal.lib.common

class TreeNode<T : Any>(
    val value: T,
    val descendants: List<TreeNode<T>>
) {

    fun find(predicate: (TreeNode<T>) -> Boolean): TreeNode<T>? {
        if (predicate(this)) {
            return this
        }
        descendants.forEach { node ->
            val result = node.find(predicate)
            if (result != null) {
                return result
            }
        }
        return null
    }

    override fun toString(): String {
        return "TreeNode($value)"
    }

    class NodeToBuild<T : Any>(
        val value: T,
        val descendants: MutableList<NodeToBuild<T>> = mutableListOf()
    )

    class Builder<T : Any> private constructor(
        private val treeNodeToBuild: NodeToBuild<T>
    ) {

        constructor(value: T) : this(NodeToBuild<T>(value, mutableListOf()))

        fun descendant(node: NodeToBuild<T>): Builder<T> {
            treeNodeToBuild.descendants.add(node)
            return this
        }

        fun descendant(value: T): Builder<T> {
            val newTree = NodeToBuild(value)
            treeNodeToBuild.descendants.add(newTree)
            return this
        }


        fun fork(value: T, action: (Builder<T>) -> Builder<T>): Builder<T> {
            val newTree = NodeToBuild(value)
            treeNodeToBuild.descendants.add(newTree)
            action(Builder(newTree))
            return this
        }

        fun build(): TreeNode<T> = treeNodeToBuild.build()

        private fun NodeToBuild<T>.build(): TreeNode<T> = TreeNode(
            value = value,
            descendants = descendants.map { it.build() }
        )
    }
}