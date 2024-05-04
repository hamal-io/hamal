package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.NodeType

class GeneratorRegistry(nodeCompilers: List<NodeCompiler>) {

    fun register(nodeCompiler: NodeCompiler): GeneratorRegistry {
        generators.putIfAbsent(nodeCompiler.type, mutableListOf())
        if (find(nodeCompiler) == null) {
            generators[nodeCompiler.type]?.add(nodeCompiler)
        }
        return this
    }

    fun register(registry: GeneratorRegistry) {
        registry.generators.values.flatten().forEach { generator -> register(generator) }
    }

    operator fun get(type: NodeType, inputTypes: List<ValueType>, outputTypes: List<ValueType>) =
        find(type, inputTypes, outputTypes)
            ?: throw NoSuchElementException(
                "No generator found for $type with [${inputTypes.joinToString(", ")}] and [${
                    outputTypes.joinToString(
                        ", "
                    )
                }]"
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

val defaultGeneratorRegistry = GeneratorRegistry(
    listOf(
        DecisionAnd.Boolean,
        Init.Boolean,
        Init.Number,
        Init.String,
    )
)