package io.hamal.repository.sqlite.new_log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.*
import java.nio.file.Path


class LogBrokerSqliteRepository(
    val path: Path
) : LogBrokerRepository {

    override fun append(cmdId: CmdId, topicId: LogTopicId, bytes: ByteArray) {
        TODO()
    }

    override fun commit(consumerId: LogConsumerId, topicId: LogTopicId, eventId: LogEventId) {
        TODO()
    }

    override fun consume(consumerId: LogConsumerId, topicId: LogTopicId, limit: Limit): List<LogEvent> {
        TODO()
    }

    override fun countTopics(query: LogBrokerRepository.LogTopicQuery): Count {
        TODO()
    }

    override fun countConsumers(query: LogBrokerRepository.LogConsumerQuery): Count {
        TODO()
    }

    override fun listEvents(query: LogBrokerRepository.LogEventQuery): List<LogEvent> {
        TODO("Not yet implemented")
    }

    override fun countEvents(query: LogBrokerRepository.LogEventQuery): Count {
        TODO("Not yet implemented")
    }

    override fun create(cmdId: CmdId, topicToCreate: LogBrokerRepository.LogTopicToCreate): LogTopic {
        TODO()
    }

    override fun findTopic(topicId: LogTopicId): LogTopic? {
        TODO()
    }


    override fun listTopics(query: LogBrokerRepository.LogTopicQuery): List<LogTopic> {
        TODO()
    }

    override fun read(firstId: LogEventId, topicId: LogTopicId, limit: Limit): List<LogEvent> {
        TODO()
    }

    override fun clear() {
//        topics.clear()
//        consumers.clear()
        TODO()
    }

    override fun close() {
        TODO()
    }
}


