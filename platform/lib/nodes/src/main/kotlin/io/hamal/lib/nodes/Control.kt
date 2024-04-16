package io.hamal.lib.nodes

import io.hamal.lib.nodes.control.ControlId
import io.hamal.lib.nodes.control.ControlInput
import io.hamal.lib.nodes.control.ControlType
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueNumber
import io.hamal.lib.typesystem.value.ValueString

data class ControlCheckbox(
    override val id: ControlId,
    override val nodeId: NodeId,
    override val port: PortInput,
    val expectedValue: ValueBoolean
) : ControlInput {
    override val type: ControlType = ControlType("Checkbox")
}

data class ControlCapture(
    override val id: ControlId,
    override val nodeId: NodeId,
    override val port: PortInput,
) : ControlInput {
    override val type: ControlType = ControlType("Test_Capture")
}

data class ControlNumberInput(
    override val id: ControlId,
    override val nodeId: NodeId,
    override val port: PortInput,
    val defaultValue: ValueNumber
) : ControlInput {
    override val type: ControlType = ControlType("NumberInput")
}

data class ControlTextArea(
    override val id: ControlId,
    override val nodeId: NodeId,
    override val port: PortInput,
    val defaultValue: ValueString
) : ControlInput {
    override val type: ControlType = ControlType("TextArea")
}