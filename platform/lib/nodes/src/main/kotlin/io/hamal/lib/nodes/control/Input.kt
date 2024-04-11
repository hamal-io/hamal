package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.PortInput
import io.hamal.lib.nodes.control.Control.Type
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueDecimal
import io.hamal.lib.typesystem.value.ValueString

interface ControlInput : Control {
    val port: PortInput
}

data class ControlInputBoolean(
    override val port: PortInput,
    val defaultValue: ValueBoolean
) : ControlInput {
    override val type: Type get() = Type.InputBoolean
}

data class ControlInputDecimal(
    override val port: PortInput,
    val defaultValue: ValueDecimal
) : ControlInput {
    override val type: Type get() = Type.InputDecimal
}

data class ControlInputString(
    override val port: PortInput,
    val defaultValue: ValueString
) : ControlInput {
    override val type: Type get() = Type.InputString
}