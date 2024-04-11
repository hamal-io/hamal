package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeNew


interface Generator {
    val type: NodeType
    val inputTypes: List<TypeNew>
    val outputTypes: List<TypeNew>

    fun toCode(node: Node): String
}

object GeneratorRegistry {

    fun register(generator: Generator) {
        generators[generator.type] = generator
    }

    operator fun get(type: NodeType) = generators[type] ?: throw NoSuchElementException("No generator found for $type")

    private val generators = mutableMapOf<NodeType, Generator>()
}