package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlExtension
import io.hamal.lib.common.value.ValueType


data class PortInputExtension(
    val inputType: ValueType
)

data class PortOutputExtension(
    val outputType: ValueType
)

data class NodeExtension(
    val type: NodeType,
    val title: NodeTitle,
    val size: Size,
    val controls: List<ControlExtension>,
    val outputs: List<PortOutputExtension>
)