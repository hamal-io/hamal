package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.TypeBoolean
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueTrue
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.ControlInputBoolean
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType

sealed class Filter : NodeCompiler() {
    override val type: NodeType get() = NodeType("Filter")

    data object Boolean : Filter() {
        override val inputTypes: List<ValueType> get() = listOf(TypeBoolean)
        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean)

        override fun toCode(ctx: NodeCompiler.Context): ValueCode {
            val checkbox = ctx.controls.filterIsInstance<ControlInputBoolean>().first()
            val expectedValue = checkbox.value
            return ValueCode(
                """
                local expected = ${if (expectedValue == ValueTrue) "true" else "false"} 
                if arg_1 == expected then
                    return arg_1
                else 
                    return nil
                end
            """.trimIndent()
            )
        }
    }

}