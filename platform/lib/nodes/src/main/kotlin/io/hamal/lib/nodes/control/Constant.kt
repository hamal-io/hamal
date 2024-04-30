package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.value.value.ValueBoolean
import io.hamal.lib.value.value.ValueDecimal
import io.hamal.lib.value.value.ValueString

sealed interface ControlConstant : Control

data class ControlConstantBoolean(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    val value: ValueBoolean
) : ControlConstant {
    override val type: ControlType = ControlType("ConstantBoolean")
}

data class ControlConstantDecimal(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    val value: ValueDecimal
) : ControlConstant {
    override val type: ControlType = ControlType("ConstantDecimal")
}

data class ControlConstantString(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    val value: ValueString
) : ControlConstant {
    override val type: ControlType = ControlType("ConstantString")
}