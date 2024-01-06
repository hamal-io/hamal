package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.KuaMap


data class EventToSubmit(
    val topicName: TopicName,
    val payload: EventPayload
)

data class Event(
    val topic: EventTopic,
    val id: EventId,
    val payload: EventPayload
)

data class EventTopic(
    val id: TopicId,
    val name: TopicName
)


data class EventId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

class EventPayload(override val value: KuaMap = KuaMap()) : MapValueObject()
