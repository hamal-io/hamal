package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import java.time.Instant

class LogEntryId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

data class LogEntry(
    val id: LogEntryId,
    val topicId: LogTopicId,
    val segmentId: LogSegmentId,
    val bytes: ByteArray,
    val instant: Instant
)