package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.TypeBoolean
import io.hamal.lib.common.value.ValueTrue
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.Control
import io.hamal.lib.nodes.ControlCheckbox
import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType

sealed interface DecisionAnd : NodeCompiler {
    override val type: NodeType get() = NodeType("Decision")

    data object Boolean : DecisionAnd {
        override val inputTypes: List<ValueType> get() = listOf(TypeBoolean)
        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean, TypeBoolean)

        override fun toCode(node: Node, controls: List<Control>): String {
            val checkbox = controls.filterIsInstance<ControlCheckbox>().first()
            val expectedValue = checkbox.value
            return """
                if arg_1 == ${if (expectedValue == ValueTrue) "true" else "false"} then
                    return true, nil
                else 
                    return nil, false
                end
            """.trimIndent()
        }
    }
}