package io.hamal.backend.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface LogBrokerTopicsRepository : Closeable {
    fun create(cmdId: CmdId, toCreate: TopicToCreate): LogTopic
    fun find(name: TopicName): LogTopic?
    fun find(id: TopicId): LogTopic?
    fun list(): List<LogTopic>
    fun count(): ULong
    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName
    )
}
