package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.*
import java.time.Instant

class FuncId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun FuncId(value: SnowflakeId) = FuncId(ValueSnowflakeId(value))
        fun FuncId(value: Int) = FuncId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun FuncId(value: String) = FuncId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}


class FuncName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun FuncName(value: String) = FuncName(ValueString(value))
    }
}

class FuncInputs(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class DeployMessage(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun DeployMessage(value: String) = DeployMessage(ValueString(value))
        val empty = DeployMessage("")
    }
}

class DeployedAt(override val value: ValueInstant) : ValueVariableInstant() {
    companion object {
        @JvmStatic
        fun now(): DeployedAt = DeployedAt(TimeUtils.now())
        fun DeployedAt(value: Instant) = DeployedAt(ValueInstant(value))
    }
}
