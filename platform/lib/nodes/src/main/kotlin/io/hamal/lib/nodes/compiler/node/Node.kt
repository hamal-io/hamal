package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.nodes.Control
import io.hamal.lib.nodes.Form
import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType


abstract class AbstractNode {
    abstract val type: NodeType

    abstract val inputs: List<Form>
    abstract val outputs: List<Form>

    abstract fun toCode(ctx: Context): ValueCode

    data class Context(
        val node: Node,
        val controls: List<Control>,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractNode

        if (type != other.type) return false
        if (inputs != other.inputs) return false
        if (outputs != other.outputs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + inputs.hashCode()
        result = 31 * result + outputs.hashCode()
        return result
    }

}
