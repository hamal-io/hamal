package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId

data class ControlCondition(
    override val id: ControlId,
    override val nodeId: NodeId
) : Control {
    override val type: ControlType = ControlType.Condition
}