package io.hamal.lib.nodes.control

import io.hamal.lib.common.value.ValueBoolean
import io.hamal.lib.common.value.ValueDecimal
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.nodes.NodeId

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