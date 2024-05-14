package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.TypeBoolean
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.ControlBooleanInput
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType

sealed class Input : NodeCompiler() {
    override val type: NodeType get() = NodeType("Input")
    override val inputTypes: List<ValueType> get() = listOf()

    data object Boolean : Input() {
        override val outputTypes: List<ValueType> get() = listOf(TypeBoolean)

        override fun toCode(ctx: NodeCompiler.Context): ValueCode {
            val control = ctx.controls.filterIsInstance<ControlBooleanInput>().firstOrNull()
                ?: throw IllegalStateException("Boolean input has no ControlBooleanInput")
            return ValueCode(
                """
                return ${control.value.booleanValue}
            """.trimIndent()
            )
        }
    }
}