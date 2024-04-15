package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlId
import io.hamal.lib.nodes.control.ControlInput
import io.hamal.lib.nodes.control.ControlType
import io.hamal.lib.typesystem.value.ValueString

data class ControlTextArea(
    override val id: ControlId,
    override val nodeId: NodeId,
    override val port: PortInput,
    val defaultValue: ValueString
) : ControlInput {
    override val type: ControlType = ControlType.TextArea
}