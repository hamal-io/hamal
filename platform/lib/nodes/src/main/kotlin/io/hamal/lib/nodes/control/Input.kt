package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.PortInput
import io.hamal.lib.nodes.PortInputExtension
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueDecimal
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
    override val type: ControlType get() = ControlType.InputBoolean
}

data class ControlInputDecimal(
    override val port: PortInput,
    val defaultValue: ValueDecimal
) : ControlInput {
    override val type: ControlType get() = ControlType.InputDecimal
}

data class ControlInputString(
    override val port: PortInput,
    val defaultValue: ValueString
) : ControlInput {
    override val type: ControlType get() = ControlType.InputString
}

data class ControlExtensionInputString(
    override val port: PortInputExtension,
    val defaultValue: ValueString?
) : ControlExtensionInput {
    override val type: ControlType get() = ControlType.InputString
}