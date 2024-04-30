package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.NodeType
import io.hamal.lib.value.type.Type

class GeneratorRegistry(generators: List<Generator>) {

    fun register(generator: Generator): GeneratorRegistry {
        generators.putIfAbsent(generator.type, mutableListOf())
        if (find(generator) == null) {
            generators[generator.type]?.add(generator)
        }
        return this
    }

    fun register(registry: GeneratorRegistry) {
        registry.generators.values.flatten().forEach { generator -> register(generator) }
    }

    operator fun get(type: NodeType, inputTypes: List<Type>, outputTypes: List<Type>) =
        find(type, inputTypes, outputTypes)
            ?: throw NoSuchElementException(
                "No generator found for $type with [${inputTypes.joinToString(", ")}] and [${
                    outputTypes.joinToString(
                        ", "
                    )
                }]"
            )

    fun find(type: NodeType, inputTypes: List<Type>, outputTypes: List<Type>): Generator? {
        return generators[type]?.find {
            it.inputTypes == inputTypes && it.outputTypes == outputTypes
        }
    }

    private fun find(generator: Generator): Generator? {
        return find(generator.type, generator.inputTypes, generator.outputTypes)
    }

    private val generators = mutableMapOf<NodeType, MutableList<Generator>>()

    init {
        generators.forEach(::register)
    }
}

val defaultGeneratorRegistry = GeneratorRegistry(
    listOf(
        GeneratorDecision.Boolean,
        GeneratorInit.Boolean,
        GeneratorInit.Number,
        GeneratorInit.String,
    )
)