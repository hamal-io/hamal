package io.hamal.repository.api.log

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import java.time.Instant

class LogEventId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

data class LogEvent(
    val id: LogEventId,
    val topicId: LogTopicId,
    val segmentId: LogSegmentId,
    val bytes: ByteArray,
    val instant: Instant
)