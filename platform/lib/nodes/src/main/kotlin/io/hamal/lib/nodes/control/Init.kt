package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.control.ControlType.Companion.ControlType

data class ControlInit(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    val selector: String = "__nodes__init__",
    val description: String = ""
) : Control {
    override val type: ControlType = ControlType("Init")
}