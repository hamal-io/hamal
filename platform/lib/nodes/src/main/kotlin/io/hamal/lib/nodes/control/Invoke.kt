package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.PortInput

data class ControlInvoke(
    override val id: ControlId,
    override val nodeId: NodeId,
    override val port: PortInput
) : ControlInput {
    override val type: ControlType = ControlType("Invoke")
}