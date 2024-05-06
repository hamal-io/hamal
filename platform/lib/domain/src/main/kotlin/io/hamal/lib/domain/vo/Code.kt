package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.CodeTypes

class CodeId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun CodeId(value: SnowflakeId) = CodeId(ValueSnowflakeId(value))
        fun CodeId(value: Int) = CodeId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun CodeId(value: String) = CodeId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class CodeVersion(override val value: ValueNumber) : ValueVariableNumber() {
    init {
        require(value > ValueNumber(0)) { "CodeVersion must be positive" }
    }

    companion object {
        fun CodeVersion(value: Int) = CodeVersion(ValueNumber(value))
    }
}

class CodeType(override val value: ValueEnum) : ValueVariableEnum<CodeTypes>(CodeTypes::class) {
    companion object {
        fun CodeType(value: Enum<CodeTypes>) = CodeType(ValueEnum(value.name))
    }
}