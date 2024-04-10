package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.control.Control.Type

data class ControlCondition(
    override val id: ControlId
) : Control {
    override val type: Type get() = Type.Condition
}