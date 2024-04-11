package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.NodeType
import io.hamal.lib.typesystem.TypeNew

class GeneratorRegistry(generators: List<Generator>) {

    fun register(generator: Generator): GeneratorRegistry {
        generators.putIfAbsent(generator.type, mutableListOf())
        if (find(generator) == null) {
            generators[generator.type]?.add(generator)
        }
        return this
    }

    operator fun get(type: NodeType, inputTypes: List<TypeNew>) = find(type, inputTypes)
        ?: throw NoSuchElementException("No generator found for $type with [${inputTypes.joinToString(", ")}]")

    fun find(type: NodeType, inputTypes: List<TypeNew>): Generator? {
        return generators[type]?.find { it.inputTypes == inputTypes }
    }

    private fun find(generator: Generator): Generator? {
        return find(generator.type, generator.inputTypes)
    }

    private val generators = mutableMapOf<NodeType, MutableList<Generator>>()

    init {
        generators.forEach(::register)
    }
}