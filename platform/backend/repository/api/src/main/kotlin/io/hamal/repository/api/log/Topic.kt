package io.hamal.repository.api.log

import io.hamal.lib.common.domain.*
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.CmdRepository


data class LogTopic(
    val id: LogTopicId,
    val createdAt: CreatedAt,
    val updatedAt: UpdatedAt
)

interface LogTopicRepository : CmdRepository {
    fun append(cmdId: CmdId, bytes: ByteArray)

    fun read(firstId: LogEventId, limit: Limit = Limit.One): List<LogEvent>

    fun countEvents(): Count
}
