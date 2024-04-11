package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.control.Control.Type
import io.hamal.lib.typesystem.value.ValueBoolean
import io.hamal.lib.typesystem.value.ValueDecimal
import io.hamal.lib.typesystem.value.ValueString

sealed interface ControlConstant : Control

data class ControlConstantBoolean(
    val value: ValueBoolean
) : ControlConstant {
    override val type: Type get() = Type.ConstantBoolean
}

data class ControlConstantDecimal(
    val value: ValueDecimal
) : ControlConstant {
    override val type: Type get() = Type.ConstantDecimal
}

data class ControlConstantString(
    val value: ValueString
) : ControlConstant {
    override val type: Type get() = Type.ConstantString
}