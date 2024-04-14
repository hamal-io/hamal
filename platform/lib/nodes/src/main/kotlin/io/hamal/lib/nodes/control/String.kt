package io.hamal.lib.nodes.control

data class ControlString(
    override val id: ControlId,
    val text: String?,
    val placeholder: String?,
) : Control {
    override val type: ControlType = ControlType.String
}