package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId

data class ControlInit(
    override val id: ControlId,
    override val nodeId: NodeId,
    val selector: String = "__nodes__init__",
    val description: String = ""
) : Control {
    override val type: ControlType = ControlType.Init
}