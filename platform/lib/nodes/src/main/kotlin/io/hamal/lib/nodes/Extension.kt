package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlExtension
import io.hamal.lib.typesystem.Type


data class PortInputExtension(
    val inputType: Type
)

data class PortOutputExtension(
    val outputType: Type
)

data class NodeExtension(
    val type: NodeType,
    val title: NodeTitle,
    val size: Size,
    val controls: List<ControlExtension>,
    val outputs: List<PortOutputExtension>
)