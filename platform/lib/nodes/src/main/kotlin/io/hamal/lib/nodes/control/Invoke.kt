package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.PortId

data class ControlInvoke(
    val id: PortId,
) : Control {
    override val type: ControlType = ControlType.Invoke
}