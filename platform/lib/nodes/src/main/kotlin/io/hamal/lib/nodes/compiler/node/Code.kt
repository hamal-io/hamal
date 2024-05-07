package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.TypeObject
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.ControlCode
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.NodeType.Companion.NodeType
import io.hamal.lib.nodes.compiler.node.NodeCompiler.Context

interface Code : NodeCompiler {
    override val type: NodeType get() = NodeType("Code")
    override val outputTypes: List<ValueType> get() = listOf()

    data object Object : Code {
        override val inputTypes: List<ValueType> = listOf(TypeObject)

        override fun toCode(ctx: Context): ValueCode {
            val code = ctx.controls.filterIsInstance<ControlCode>().first().value
            return code
        }
    }

}