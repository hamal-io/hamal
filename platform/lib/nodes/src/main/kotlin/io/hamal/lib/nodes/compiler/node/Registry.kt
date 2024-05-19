package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.logger
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeVersion

private val log = logger(NodeCompilerRegistry::class)

class NodeCompilerRegistry(nodeCompilers: List<AbstractNode>) {

    fun register(node: AbstractNode): NodeCompilerRegistry {
        generators.putIfAbsent(node.type, mutableListOf())
        if (find(node) == null) {
            generators[node.type]?.add(node)
        } else {
            log.warn("${node::class.simpleName} already registered")
        }
        return this
    }

    fun register(registry: NodeCompilerRegistry) {
        registry.generators.values.flatten().forEach { generator -> register(generator) }
    }

    operator fun get(type: NodeType, version: NodeVersion) =
        find(type, version) ?: throw NoSuchElementException("No generator found for $type with $version")

    fun find(node: AbstractNode): AbstractNode? = find(node.type, node.version)

    fun find(type: NodeType, version: NodeVersion): AbstractNode? {
        return generators[type]?.find { it.version == version }
    }

    private val generators = mutableMapOf<NodeType, MutableList<AbstractNode>>()

    init {
        nodeCompilers.forEach(::register)
    }
}

val defaultNodeCompilerRegistry = NodeCompilerRegistry(
    listOf(
//        Code.Object,
//        Code.String,
//        Decision.Boolean,
//        Filter.Boolean,
        Init.V_0_0_1,
        Print.V_0_0_1
//        Input.Boolean,
//        Input.String,
//        Print.Boolean,
//        Print.Number,
//        Print.Object,
//        Print.String,
    )
)