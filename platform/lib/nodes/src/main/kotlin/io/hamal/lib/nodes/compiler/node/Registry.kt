package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.logger
import io.hamal.lib.nodes.Form
import io.hamal.lib.nodes.NodeType

private val log = logger(NodeCompilerRegistry::class)

class NodeCompilerRegistry(nodeCompilers: List<AbstractNode>) {

    fun register(nodeCompiler: AbstractNode): NodeCompilerRegistry {
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

    operator fun get(type: NodeType, inputs: List<Form>, outputs: List<Form>) =
        find(type, inputs, outputs)
            ?: throw NoSuchElementException(
                "No generator found for $type with [${inputs.joinToString(", ")}] and [${outputs.joinToString(", ")}]"
            )

    fun find(type: NodeType, inputs: List<Form>, outputs: List<Form>): AbstractNode? {
        return generators[type]?.find {
            it.inputs == inputs && it.outputs == outputs
        }
    }

    private fun find(nodeCompiler: AbstractNode): AbstractNode? {
        return find(nodeCompiler.type, nodeCompiler.inputs, nodeCompiler.outputs)
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
//        Input.Boolean,
//        Input.String,
//        Print.Boolean,
//        Print.Number,
//        Print.Object,
//        Print.String,
    )
)