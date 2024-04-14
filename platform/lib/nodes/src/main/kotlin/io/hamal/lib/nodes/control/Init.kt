package io.hamal.lib.nodes.control

data class ControlInit(
    val selector: String = "__nodes__init__",
    val description: String = ""
) : Control {
    override val type: ControlType = ControlType.Init
}