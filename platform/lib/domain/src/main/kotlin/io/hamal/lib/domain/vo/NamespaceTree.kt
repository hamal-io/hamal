package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId

class NamespaceTreeId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        val root = NamespaceTreeId(1337)

        fun NamespaceTreeId(value: SnowflakeId) = NamespaceTreeId(ValueSnowflakeId(value))
        fun NamespaceTreeId(value: Int) = NamespaceTreeId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun NamespaceTreeId(value: String) = NamespaceTreeId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}