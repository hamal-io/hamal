package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.repository.api.CmdRepository

fun interface LogTopicCreate {

    fun create(cmdId: CmdId, topicToCreate: ToCreate): LogTopic

    data class ToCreate(
        val id: LogTopicId,
        val name: LogTopicName,
        val groupId: LogTopicGroupId
    )
}

fun interface LogTopicAppend {
    fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray)
}

fun interface LogTopicConsume {
    fun consume(consumerId: LogTopicConsumerId, topicId: LogTopicId, limit: Limit): List<LogEntry>
}

fun interface LogTopicRead {
    fun read(firstId: LogEntryId, topicId: LogTopicId, limit: Limit): List<LogEntry>
}

fun interface LogTopicCommit {
    fun commit(consumerId: LogTopicConsumerId, topicId: LogTopicId, entryId: LogEntryId)
}

fun interface LogTopicResolve {
    fun resolveTopic(groupId: LogTopicGroupId, name: LogTopicName): LogTopic?
}

fun interface LogTopicFind {
    fun findTopic(topicId: LogTopicId): LogTopic?
}

fun interface LogTopicCount {
    fun count(query: LogTopicQuery): ULong
}

fun interface LogTopicList {
    fun list(query: LogTopicQuery): List<LogTopic>
}

data class LogTopicQuery(
    var afterId: LogTopicId = LogTopicId(SnowflakeId(Long.MAX_VALUE)),
    var names: List<LogTopicName> = listOf(),
    var limit: Limit = Limit(1),
    var groupIds: List<LogTopicGroupId> = listOf()
)

interface LogBrokerRepository :
    CmdRepository,
    LogTopicAppend,
    LogTopicCommit,
    LogTopicConsume,
    LogTopicCount,
    LogTopicCreate,
    LogTopicFind,
    LogTopicList,
    LogTopicRead,
    LogTopicResolve