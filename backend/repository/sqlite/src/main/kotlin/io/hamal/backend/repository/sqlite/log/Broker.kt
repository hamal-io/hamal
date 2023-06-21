package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.*
import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName


class SqliteLogBrokerRepository(
    logBroker: LogBroker
) : LogBrokerRepository {

    internal val consumersRepository: LogBrokerConsumersRepository
    internal val topicsRepository: LogBrokerTopicsRepository

    init {
        topicsRepository = SqliteLogBrokerTopicsRepository(
            BrokerTopics(
                logBrokerId = logBroker.id,
                path = logBroker.path
            )
        )
        consumersRepository = SqliteLogBrokerConsumersRepository(
            BrokerConsumers(
                logBrokerId = logBroker.id,
                path = logBroker.path
            )
        )
    }

    private val logTopicRepositoryMapping = KeyedOnce.default<LogTopic, LogTopicRepository>()

    override fun resolveTopic(topicName: TopicName) = topicsRepository.resolveTopic(topicName)

    override fun append(cmdId: CmdId, topic: LogTopic, bytes: ByteArray) {
        resolveRepository(topic).append(cmdId, bytes)
    }

    override fun close() {
        topicsRepository.close()
        consumersRepository.close()
        logTopicRepositoryMapping.keys().forEach { topic ->
            resolveRepository(topic).close()
        }
    }

    override fun consume(groupId: GroupId, topic: LogTopic, limit: Int): List<LogChunk> {
        val nextChunkId = consumersRepository.nextChunkId(groupId, topic.id)
        return resolveRepository(topic).read(nextChunkId, limit)
    }

    override fun commit(groupId: GroupId, topic: LogTopic, chunkId: LogChunkId) {
        consumersRepository.commit(groupId, topic.id, chunkId)
    }

    override fun find(topicId: TopicId) = topicsRepository.find(topicId)

    override fun topics(): Set<LogTopic> {
        return logTopicRepositoryMapping.keys()
    }

    override fun read(lastId: LogChunkId, topic: LogTopic, limit: Int): List<LogChunk> {
        return resolveRepository(topic).read(lastId, limit)
    }

    private fun resolveRepository(topic: LogTopic) = logTopicRepositoryMapping(topic) {
        SqliteLogTopicRepository(topic)
    }
}