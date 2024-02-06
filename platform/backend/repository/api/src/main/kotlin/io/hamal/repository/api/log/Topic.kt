package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.CmdRepository


interface LogTopic {
    val id: LogTopicId
}

interface LogTopicRepository : CmdRepository {
    fun append(cmdId: CmdId, bytes: ByteArray)

    fun read(firstId: LogEventId, limit: Limit = Limit.One): List<LogEvent>

    fun countEvents(): Count
}
