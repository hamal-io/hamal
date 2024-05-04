package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.TypeBoolean
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueTrue
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.ControlCheckbox
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context

sealed interface DecisionAnd : NodeCompiler {
    override val type: NodeType get() = NodeType("Decision")

    data object Boolean : DecisionAnd {
        override val inputTypes: List<ValueType> get() = listOf(TypeBoolean)
        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean, TypeBoolean)

        override fun toCode(ctx: Context): ValueCode {
            val checkbox = ctx.controls.filterIsInstance<ControlCheckbox>().first()
            val expectedValue = checkbox.value
            return ValueCode(
                """
                if arg_1 == ${if (expectedValue == ValueTrue) "true" else "false"} then
                    return true, nil
                else 
                    return nil, false
                end
            """.trimIndent()
            )
        }
    }
}