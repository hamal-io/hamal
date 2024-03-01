package io.hamal.repository.api.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.CmdRepository

interface LogBrokerRepository : CmdRepository {

    fun create(cmd: CreateTopicCmd): LogTopic

    data class CreateTopicCmd(
        val id: CmdId,
        val logTopicId: LogTopicId
    )

    fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray)

    fun consume(consumerId: LogConsumerId, topicId: LogTopicId, limit: Limit): List<LogEvent>

    fun read(firstId: LogEventId, topicId: LogTopicId, limit: Limit): List<LogEvent>

    fun commit(consumerId: LogConsumerId, topicId: LogTopicId, eventId: LogEventId)

    fun findTopic(topicId: LogTopicId): LogTopic?

    fun getTopic(topicId: LogTopicId): LogTopic = findTopic(topicId) ?: throw NoSuchElementException("Topic not found")

    fun countTopics(query: LogTopicQuery): Count

    fun listTopics(query: LogTopicQuery): List<LogTopic>

    fun countConsumers(query: LogConsumerQuery): Count

    fun listEvents(query: LogEventQuery): List<LogEvent>

    fun countEvents(query: LogEventQuery): Count

    data class LogTopicQuery(
        var afterId: LogTopicId = LogTopicId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )

    data class LogConsumerQuery(
        var afterId: LogConsumerId = LogConsumerId(SnowflakeId(Long.MAX_VALUE)),
        var limit: Limit = Limit(1)
    )

    data class LogEventQuery(
        var topicId: LogTopicId,
        var afterId: LogEventId = LogEventId(SnowflakeId(0)),
        var limit: Limit = Limit(1)
    )
}