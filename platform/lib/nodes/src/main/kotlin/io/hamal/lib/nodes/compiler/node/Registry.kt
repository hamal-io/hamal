package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.logger
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.NodeType

private val log = logger(NodeCompilerRegistry::class)

class NodeCompilerRegistry(nodeCompilers: List<NodeCompiler>) {

    fun register(nodeCompiler: NodeCompiler): NodeCompilerRegistry {
        generators.putIfAbsent(nodeCompiler.type, mutableListOf())
        if (find(nodeCompiler) == null) {
            generators[nodeCompiler.type]?.add(nodeCompiler)
        } else {
            log.warn("${nodeCompiler::class.simpleName} already registered")
        }
        return this
    }

    fun register(registry: NodeCompilerRegistry) {
        registry.generators.values.flatten().forEach { generator -> register(generator) }
    }

    operator fun get(type: NodeType, inputTypes: List<ValueType>, outputTypes: List<ValueType>) =
        find(type, inputTypes, outputTypes)
            ?: throw NoSuchElementException(
                "No generator found for $type with [${inputTypes.joinToString(", ")}] and [${outputTypes.joinToString(", ")}]"
            )

    fun find(type: NodeType, inputTypes: List<ValueType>, outputTypes: List<ValueType>): NodeCompiler? {
        return generators[type]?.find {
            it.inputTypes == inputTypes && it.outputTypes == outputTypes
        }
    }

    private fun find(nodeCompiler: NodeCompiler): NodeCompiler? {
        return find(nodeCompiler.type, nodeCompiler.inputTypes, nodeCompiler.outputTypes)
    }

    private val generators = mutableMapOf<NodeType, MutableList<NodeCompiler>>()

    init {
        nodeCompilers.forEach(::register)
    }
}

val defaultNodeCompilerRegistry = NodeCompilerRegistry(
    listOf(
        Code.Object,
        Code.String,
        Decision.Boolean,
        Filter.Boolean,
        Input.Boolean,
        Input.String,
        Print.Boolean,
        Print.Number,
        Print.Object,
        Print.String,
    )
)