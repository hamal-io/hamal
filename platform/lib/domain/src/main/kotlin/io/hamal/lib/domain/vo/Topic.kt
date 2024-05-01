package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueSnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableSnowflakeId
import io.hamal.lib.common.value.ValueVariableString

class LogTopicId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun LogTopicId(value: SnowflakeId) = LogTopicId(ValueSnowflakeId(value))
        fun LogTopicId(value: Int) = LogTopicId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun LogTopicId(value: String) = LogTopicId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class TopicId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun TopicId(value: SnowflakeId) = TopicId(ValueSnowflakeId(value))
        fun TopicId(value: Int) = TopicId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun TopicId(value: String) = TopicId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class TopicName(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun TopicName(value: String) = TopicName(ValueString(value))
    }
}

class TopicEventId(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId() {
    companion object {
        fun TopicEventId(value: SnowflakeId) = TopicEventId(ValueSnowflakeId(value))
        fun TopicEventId(value: Int) = TopicEventId(ValueSnowflakeId(SnowflakeId(value.toLong())))
        fun TopicEventId(value: String) = TopicEventId(ValueSnowflakeId(SnowflakeId(value.toLong(16))))
    }
}

class TopicEventPayload(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()
