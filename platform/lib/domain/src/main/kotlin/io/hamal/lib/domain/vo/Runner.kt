package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId

class RunnerId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun RunnerId(value: SnowflakeId) = RunnerId(ValueSnowflakeId(value))
        fun RunnerId(value: Int) = RunnerId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun RunnerId(value: String) = RunnerId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}


class RunnerEnv(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()