package io.hamal.lib.nodes.control

data class ControlString(
    val text: String?,
    val placeholder: String?,
) : Control {
    override val type: ControlType get() = ControlType.String
}