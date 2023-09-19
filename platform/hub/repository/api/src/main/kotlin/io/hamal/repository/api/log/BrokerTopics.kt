package io.hamal.repository.api.log

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import java.io.Closeable

interface BrokerTopicsRepository : Closeable {
    fun create(cmdId: CmdId, toCreate: TopicToCreate): Topic
    fun find(groupId: GroupId, name: TopicName): Topic?
    fun find(id: TopicId): Topic?
    fun list(query: TopicQuery): List<Topic>
    fun count(query: TopicQuery): ULong

    data class TopicToCreate(
        val id: TopicId,
        val name: TopicName,
        val groupId: GroupId
    )

    data class TopicQuery(
        var afterId: TopicId = TopicId(SnowflakeId(Long.MAX_VALUE)),
        var names: List<TopicName> = listOf(),
        var limit: Limit = Limit(1),
        var groupIds: List<GroupId>
    )
}
