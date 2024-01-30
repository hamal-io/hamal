package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.repository.api.CmdRepository

interface LogTopicCreate {

    fun create(cmdId: CmdId, topicToCreate: ToCreate): LogTopic

    data class ToCreate(
        val id: LogTopicId,
        val name: LogTopicName,
        val groupId: LogTopicGroupId
    )

}

interface LogTopicAppend {
    fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray)
}

interface LogTopicConsume {
    fun consume(consumerId: LogTopicConsumerId, topicId: LogTopicId, limit: Limit): List<LogEntry>
}

interface LogTopicRead {
    fun read(firstId: LogEntryId, topicId: LogTopicId, limit: Limit): List<LogEntry>
}

interface LogTopicCommit {
    fun commit(consumerId: LogTopicConsumerId, topicId: LogTopicId, entryId: LogEntryId)
}

interface LogTopicResolve {
    fun resolveTopic(groupId: LogTopicGroupId, name: LogTopicName): LogTopicName?
}

interface LogTopicFind {
    fun findTopic(topicId: LogTopicId): LogTopic?
}

interface LogBrokerRepository :
    CmdRepository,
    LogTopicAppend,
    LogTopicCommit,
    LogTopicConsume,
    LogTopicCreate,
    LogTopicFind,
    LogTopicRead,
    LogTopicResolve