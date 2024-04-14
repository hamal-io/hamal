package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.PortId

data class ControlInvoke(
    override val id: ControlId,
    val portId: PortId,
) : Control {
    override val type: ControlType = ControlType.Invoke
}