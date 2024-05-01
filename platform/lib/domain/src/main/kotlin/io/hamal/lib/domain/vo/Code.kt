package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectInt
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId

class CodeId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun CodeId(value: SnowflakeId) = CodeId(ValueSnowflakeId(value))
        fun CodeId(value: Int) = CodeId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun CodeId(value: String) = CodeId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class CodeVersion(override val value: Int) : ValueObjectInt() {
    init {
        require(value > 0) { "CodeVersion must be positive" }
    }
}