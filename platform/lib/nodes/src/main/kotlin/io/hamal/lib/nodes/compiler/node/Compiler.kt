package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.Control
import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType


abstract class NodeCompiler {

    abstract val type: NodeType
    abstract val inputTypes: List<ValueType> // FIXME that should be Parameter (name:type) - so that it can be addressed in lua directly by name
    abstract val outputTypes: List<ValueType>

    abstract fun toCode(ctx: Context): ValueCode

    data class Context(
        val node: Node,
        val controls: List<Control>,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NodeCompiler

        if (type != other.type) return false
        if (inputTypes != other.inputTypes) return false
        if (outputTypes != other.outputTypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + inputTypes.hashCode()
        result = 31 * result + outputTypes.hashCode()
        return result
    }

}
