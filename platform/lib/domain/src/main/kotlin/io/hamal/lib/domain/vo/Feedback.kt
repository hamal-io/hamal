package io.hamal.lib.domain.vo

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString

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