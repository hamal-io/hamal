package io.hamal.lib.nodes.control

class ControlCondition(
) : Control {
    override val type: ControlType get() = ControlType.Condition
}