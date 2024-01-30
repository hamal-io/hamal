package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import java.time.Instant

class LogEntryId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

interface LogEntry {
    val id: LogEntryId
    val topicId: LogTopicId
    val segmentId: LogSegmentId
    val bytes: ByteArray
    val instant: Instant
}