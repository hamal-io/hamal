package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.typesystem.type.Type
import io.hamal.lib.typesystem.type.TypeBoolean

sealed interface GeneratorDecision : Generator {
    override val type: NodeType get() = NodeType("DECISION")

    data object Boolean : GeneratorDecision {
        override val inputTypes: List<Type> get() = listOf(TypeBoolean)
        override val outputTypes: List<Type> get() = listOf(TypeBoolean, TypeBoolean)

        override fun toCode(node: Node, controls: List<Control>): String {
            TODO("Not yet implemented")
        }
    }
}