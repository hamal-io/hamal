package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.FeedbackMoods

class FeedbackId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun FeedbackId(value: SnowflakeId) = FeedbackId(ValueSnowflakeId(value))
        fun FeedbackId(value: Int) = FeedbackId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun FeedbackId(value: String) = FeedbackId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class FeedbackMessage(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun FeedbackMessage(value: String) = FeedbackMessage(ValueString(value))
    }
}

class FeedbackMood(override val value: ValueEnum) : ValueVariableEnum<FeedbackMoods>(FeedbackMoods::class) {
    companion object {
        fun FeedbackMood(value: Enum<FeedbackMoods>) = FeedbackMood(ValueEnum(value.name))
    }
}