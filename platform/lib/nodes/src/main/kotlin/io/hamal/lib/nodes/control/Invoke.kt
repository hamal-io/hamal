package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.PortInput
import io.hamal.lib.nodes.control.ControlType.Companion.ControlType

data class ControlInvoke(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    override val port: PortInput
) : ControlInput {
    override val type: ControlType = ControlType("Invoke")
}