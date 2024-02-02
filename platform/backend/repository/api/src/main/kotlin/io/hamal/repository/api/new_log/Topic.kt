package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.repository.api.CmdRepository

class LogTopicId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
}

class LogTopicName(override val value: String) : ValueObjectString()

class LogTopicGroupId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))

    companion object {
        val root = LogTopicGroupId(1)
    }
}

interface LogTopic {
    val id: LogTopicId
    val groupId: LogTopicGroupId
    val name: LogTopicName
}

interface LogTopicRepository : CmdRepository {
    fun append(cmdId: CmdId, bytes: ByteArray): LogEntryId

    fun read(firstId: LogEntryId, limit: Limit = Limit(1)): List<LogEntry>

    fun countEntries(): ULong
}
