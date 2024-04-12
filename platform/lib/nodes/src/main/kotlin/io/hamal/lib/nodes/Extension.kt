package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlExtension
import io.hamal.lib.typesystem.TypeNew


data class PortInputExtension(
    val inputType: TypeNew
)

data class PortOutputExtension(
    val outputType: TypeNew
)

data class NodeExtension(
    val type: NodeType,
    val title: NodeTitle,
    val size: Size,
    val controls: List<ControlExtension>,
    val outputs: List<PortOutputExtension>
)