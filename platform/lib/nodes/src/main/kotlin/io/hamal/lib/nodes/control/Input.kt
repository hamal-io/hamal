package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.PortInput
import io.hamal.lib.nodes.PortInputExtension
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueString


interface ControlExtensionInput : ControlExtension {
    val port: PortInputExtension
}

data class ControlInputBoolean(
    override val id: ControlId,
    override val nodeId: NodeId,
    override val port: PortInput,
    val defaultValue: ValueBoolean
) : ControlInput {
    override val type: ControlType = ControlType("InputBoolean")
}


data class ControlExtensionTextArea(
    override val port: PortInputExtension,
    val defaultValue: ValueString?
) : ControlExtensionInput {
    override val type: ControlType = ControlType("TextArea")
}