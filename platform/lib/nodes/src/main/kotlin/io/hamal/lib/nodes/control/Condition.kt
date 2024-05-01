package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.control.ControlType.Companion.ControlType

data class ControlCondition(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId

) : Control {
    override val type: ControlType = ControlType("Condition")
}