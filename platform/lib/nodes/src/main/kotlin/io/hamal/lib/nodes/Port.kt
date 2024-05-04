package io.hamal.lib.nodes

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueType
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId

class PortId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun PortId(value: SnowflakeId) = PortId(ValueSnowflakeId(value))
        fun PortId(value: Int) = PortId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun PortId(value: String) = PortId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}


data class PortInput(
    val id: PortId,
    val type: ValueType
)

data class PortOutput(
    val id: PortId,
    val type: ValueType
)