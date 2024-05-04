package io.hamal.lib.nodes.generator

import io.hamal.lib.nodes.Node
import io.hamal.lib.nodes.NodeType
import io.hamal.lib.nodes.control.Control
import io.hamal.lib.common.value.ValueType


interface Generator {
    val type: NodeType
    val inputTypes: List<ValueType> // FIXME that should be Parameter (name:type) - so that it can be addressed in lua directly by name
    val outputTypes: List<ValueType>

    fun toCode(node: Node, controls: List<Control>): String
}


