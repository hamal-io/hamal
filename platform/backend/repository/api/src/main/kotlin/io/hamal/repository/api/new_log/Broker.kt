package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.repository.api.CmdRepository

interface LogBrokerRepository : CmdRepository {

    fun create(cmdId: CmdId, topicToCreate: LogTopicToCreate): LogTopic

    data class LogTopicToCreate(
        val id: LogTopicId,
        val name: LogTopicName,
        val groupId: LogTopicGroupId
    )

    fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray)

    fun consume(consumerId: LogConsumerId, topicId: LogTopicId, limit: Limit): List<LogEntry>

    fun read(firstId: LogEntryId, topicId: LogTopicId, limit: Limit): List<LogEntry>

    fun commit(consumerId: LogConsumerId, topicId: LogTopicId, entryId: LogEntryId)

    fun resolveTopic(groupId: LogTopicGroupId, name: LogTopicName): LogTopic?

    fun findTopic(topicId: LogTopicId): LogTopic?

    fun getTopic(topicId: LogTopicId): LogTopic = findTopic(topicId) ?: throw NoSuchElementException("Topic not found")

    fun countTopics(query: LogTopicQuery): ULong

    fun listTopics(query: LogTopicQuery): List<LogTopic>

    fun countConsumers(query: LogConsumerQuery): ULong

    data class LogTopicQuery(
        var afterId: LogTopicId = LogTopicId(SnowflakeId(Long.MAX_VALUE)),
        var names: List<LogTopicName> = listOf(),
        var limit: Limit = Limit(1),
        var groupIds: List<LogTopicGroupId> = listOf()
    )

    data class LogConsumerQuery(
        var afterId: LogConsumerId = LogConsumerId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )
}