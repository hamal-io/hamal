package io.hamal.backend.repository.api.log

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface LogBrokerTopicsRepository<TOPIC : LogTopic> : Closeable {
    fun create(cmdId: CmdId, toCreate: TopicToCreate): TOPIC
    fun find(name: TopicName): TOPIC?
    fun find(id: TopicId): TOPIC?
    fun query(): List<TOPIC>
    fun count(): ULong
    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName
    )
}
