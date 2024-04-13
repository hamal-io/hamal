package io.hamal.lib.nodes.control

data class ControlInit(
    val description: String
) : Control {
    override val type: ControlType get() = ControlType.Init
}