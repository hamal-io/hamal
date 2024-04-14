package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.PortInput
import io.hamal.lib.nodes.PortInputExtension
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueNumber
import io.hamal.lib.typesystem.value.ValueString

interface ControlInput : Control {
    val port: PortInput
}

interface ControlExtensionInput : ControlExtension {
    val port: PortInputExtension
}

data class ControlInputBoolean(
    override val port: PortInput,
    val defaultValue: ValueBoolean
) : ControlInput {
    override val type: ControlType = ControlType.InputBoolean
}

data class ControlInputNumber(
    override val port: PortInput,
    val defaultValue: ValueNumber
) : ControlInput {
    override val type: ControlType = ControlType.InputNumber
}

data class ControlInputString(
    override val port: PortInput,
    val defaultValue: ValueString
) : ControlInput {
    override val type: ControlType = ControlType.InputString
}

data class ControlExtensionInputString(
    override val port: PortInputExtension,
    val defaultValue: ValueString?
) : ControlExtensionInput {
    override val type: ControlType = ControlType.InputString
}