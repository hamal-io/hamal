package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString

class TriggerId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun TriggerId(value: SnowflakeId) = TriggerId(ValueSnowflakeId(value))
        fun TriggerId(value: Int) = TriggerId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun TriggerId(value: String) = TriggerId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class TriggerName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun TriggerName(value: String) = TriggerName(ValueString(value))
    }
}

class TriggerInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class TriggerDuration(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun TriggerDuration(value: String) = TriggerDuration(ValueString(value))
    }
}