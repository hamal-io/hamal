package io.hamal.lib.nodes.control

data class ControlCondition(
    override val id: ControlId
) : Control {
    override val type: ControlType = ControlType.Condition
}