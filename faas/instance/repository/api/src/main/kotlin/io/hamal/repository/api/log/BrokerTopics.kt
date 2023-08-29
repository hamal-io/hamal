package io.hamal.repository.api.log

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface BrokerTopicsRepository : Closeable {
    fun create(cmdId: CmdId, toCreate: TopicToCreate): LogTopic
    fun find(name: TopicName): LogTopic?
    fun find(id: TopicId): LogTopic?
    fun list(block: TopicQuery.() -> Unit): List<LogTopic>
    fun count(block: TopicQuery.() -> Unit): ULong
    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName
    )

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var names: List<TopicName> = listOf(),
        var limit: Limit = Limit(1)
    )
}
