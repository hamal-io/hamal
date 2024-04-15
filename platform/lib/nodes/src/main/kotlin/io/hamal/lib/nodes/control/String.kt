package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId

data class ControlString(
    override val id: ControlId,
    override val nodeId: NodeId,
    val text: String?,
    val placeholder: String?,
) : Control {
    override val type: ControlType = ControlType("String")
}