package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectInstant
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString
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

class FuncInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class DeployMessage(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun DeployMessage(value: String) = DeployMessage(ValueString(value))
        val empty = DeployMessage("")
    }
}

class DeployedAt(override val value: Instant) : ValueObjectInstant() {
    companion object {
        @JvmStatic
        fun now(): DeployedAt = DeployedAt(TimeUtils.now())
    }
}
