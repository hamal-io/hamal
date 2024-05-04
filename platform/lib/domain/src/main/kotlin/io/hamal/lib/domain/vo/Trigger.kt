package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.TriggerStates

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

class TriggerStatus(override val value: ValueEnum) : ValueVariableEnum<TriggerStates>(TriggerStates::class) {
    companion object {
        fun TriggerStatus(value: Enum<TriggerStates>) = TriggerStatus(ValueEnum(value.name))
    }
}

class TriggerInputs(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class TriggerDuration(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun TriggerDuration(value: String) = TriggerDuration(ValueString(value))
    }
}