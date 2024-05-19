package io.hamal.lib.nodes

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*

class PortIndex(override val value: ValueNumber) : ValueVariableNumber() {
    companion object {
        fun PortIndex(value: Int) = PortIndex(ValueNumber(value))
        fun PortIndex(value: Long) = PortIndex(ValueNumber(value))
    }
}

data class PortInput(
    val index: PortIndex,
    val type: ValueType
)

data class PortOutput(
    val index: PortIndex,
    val type: ValueType
)

data class TemplatePortInput(
    val inputType: ValueType
)

data class TemplatePortOutput(
    val outputType: ValueType
)



