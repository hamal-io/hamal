package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueVariableSnowflakeId

data class EventId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun EventId(value: SnowflakeId) = EventId(ValueSnowflakeId(value))
        fun EventId(value: Int) = EventId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun EventId(value: String) = EventId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class EventPayload(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()
