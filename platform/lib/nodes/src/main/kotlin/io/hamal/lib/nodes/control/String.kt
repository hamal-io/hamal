package io.hamal.lib.nodes.control

import io.hamal.lib.nodes.control.Control.Type

data class ControlString(
    override val id: ControlId,
    val text: String?,
    val placeholder: String?,
) : Control {
    override val type: Type get() = Type.String
}