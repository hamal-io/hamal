package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.control.Control.Type

class ControlCondition(
) : Control {
    override val type: Type get() = Type.Condition
}