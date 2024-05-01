package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.nodes.PortInput
import io.hamal.lib.nodes.PortInputExtension
import io.hamal.lib.common.value.ValueBoolean
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.nodes.control.ControlType.Companion.ControlType


interface ControlExtensionInput : ControlExtension {
    val port: PortInputExtension
}

data class ControlInputBoolean(
    override val identifier: ControlIdentifier,
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
    override val type: ControlType = ControlType("Text_Area")
}