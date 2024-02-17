package io.hamal.lib.common


class TreeNode<T : Any>(
    val value: T,
    val descendants: List<TreeNode<T>> = listOf()
) {

    fun get(predicate: (TreeNode<T>) -> Boolean): TreeNode<T> =
        find(predicate) ?: throw NoSuchElementException("TreeNode does not exists")

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

    fun preorder(): List<T> {
        return listOf(value).plus(descendants.flatMap { it.preorder() })
    }


    override fun toString(): String {
        return "TreeNode($value)"
    }


    class Builder<T : Any> private constructor(
        private val treeNodeToBuild: TreeNodeMutable<T>
    ) {

        constructor(value: T) : this(TreeNodeMutable<T>(value, mutableListOf()))

        fun descendant(node: TreeNodeMutable<T>): Builder<T> {
            treeNodeToBuild.descendants.add(node)
            return this
        }

        fun descendant(value: T): Builder<T> {
            val newTree = TreeNodeMutable(value)
            treeNodeToBuild.descendants.add(newTree)
            return this
        }


        fun fork(value: T, action: (Builder<T>) -> Builder<T>): Builder<T> {
            val newTree = TreeNodeMutable(value)
            treeNodeToBuild.descendants.add(newTree)
            action(Builder(newTree))
            return this
        }

        fun build(): TreeNode<T> = treeNodeToBuild.build()

        private fun TreeNodeMutable<T>.build(): TreeNode<T> = TreeNode(
            value = value,
            descendants = descendants.map { it.build() }
        )
    }
}

class TreeNodeMutable<T : Any>(
    val value: T,
    val descendants: MutableList<TreeNodeMutable<T>> = mutableListOf()
) {

    fun find(predicate: (TreeNodeMutable<T>) -> Boolean): TreeNodeMutable<T>? {
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

    fun toTreeNode(): TreeNode<T> = TreeNode(
        value = value,
        descendants = descendants.map { it.toTreeNode() }
    )
}


fun <T : Any> TreeNode<T>.mutate(): TreeNodeMutable<T> {
    return TreeNodeMutable(
        value = value,
        descendants = descendants.map { it.mutate() }.toMutableList()
    )
}

