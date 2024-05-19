package io.hamal.lib.nodes

import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.PortKey.Companion.PortKey
import java.util.*

class PortIndex(override val value: ValueNumber) : ValueVariableNumber() {
    companion object {
        fun PortIndex(value: Int) = PortIndex(ValueNumber(value))
        fun PortIndex(value: Long) = PortIndex(ValueNumber(value))
    }
}

class PortKey(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun PortKey(value: String) = PortKey(ValueString(value))
        fun random() = PortKey(UUID.randomUUID().toString())
    }
}


data class PortInput(
    val index: PortIndex,
    val type: Form,
    val key: PortKey = PortKey(index.value.longValue.toString(16)),
)

data class PortOutput(
    val index: PortIndex,
    val type: Form,
    val key: PortKey = PortKey(index.value.longValue.toString(16)),
)

data class TemplatePortInput(
    val inputType: ValueType
)

data class TemplatePortOutput(
    val outputType: ValueType
)



