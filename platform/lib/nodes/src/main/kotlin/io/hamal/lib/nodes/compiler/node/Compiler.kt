package io.hamal.lib.nodes.compiler.node

import io.hamal.lib.common.value.ValueType
import io.hamal.lib.nodes.Control
import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType


interface NodeCompiler {
    val type: NodeType
    val inputTypes: List<ValueType> // FIXME that should be Parameter (name:type) - so that it can be addressed in lua directly by name
    val outputTypes: List<ValueType>

    fun toCode(node: Node, controls: List<Control>): String
}
