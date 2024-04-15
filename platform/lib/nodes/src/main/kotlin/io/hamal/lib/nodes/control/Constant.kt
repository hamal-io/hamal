package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.NodeId
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueDecimal
import io.hamal.lib.typesystem.value.ValueString

sealed interface ControlConstant : Control

data class ControlConstantBoolean(
    override val id: ControlId,
    override val nodeId: NodeId,
    val value: ValueBoolean
) : ControlConstant {
    override val type: ControlType = ControlType("ConstantBoolean")
}

data class ControlConstantDecimal(
    override val id: ControlId,
    override val nodeId: NodeId,
    val value: ValueDecimal
) : ControlConstant {
    override val type: ControlType = ControlType("ConstantDecimal")
}

data class ControlConstantString(
    override val id: ControlId,
    override val nodeId: NodeId,
    val value: ValueString
) : ControlConstant {
    override val type: ControlType = ControlType("ConstantString")
}