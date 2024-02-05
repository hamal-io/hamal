//package io.hamal.repository.sqlite.log
//
//import io.hamal.lib.common.KeyedOnce
//import io.hamal.lib.common.domain.CmdId
//import io.hamal.lib.domain.vo.FlowId
//import io.hamal.lib.domain.vo.TopicId
//import io.hamal.lib.domain.vo.TopicName
//import io.hamal.repository.api.log.*
//import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
//import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate
//import java.nio.file.Path
//
//data class BrokerSqlite(
//    val path: Path
//)
//
//class BrokerSqliteRepository(
//    private val broker: BrokerSqlite
//) : BrokerRepository {
//
//    private val consumersRepository: BrokerConsumersSqliteRepository
//    private val topicsRepository: BrokerTopicsSqliteRepository
//
//    init {
//        topicsRepository = BrokerTopicsSqliteRepository(
//            BrokerTopicsSqlite(
//                path = broker.path
//            )
//        )
//        consumersRepository = BrokerConsumersSqliteRepository(
//            BrokerConsumersSqlite(
//                path = broker.path
//            )
//        )
//    }
//
//    private val topicRepositoryMapping = KeyedOnce.default<DepTopic, DepTopicRepository>()
//
//
//    override fun create(cmdId: CmdId, topicToCreate: CreateTopic.TopicToCreate): DepTopic =
//        topicsRepository.create(
//            cmdId,
//            TopicToCreate(
//                id = topicToCreate.id,
//                name = topicToCreate.name,
//                flowId = topicToCreate.flowId,
//                groupId = topicToCreate.groupId
//            )
//        )
//
//    override fun append(cmdId: CmdId, topic: DepTopic, bytes: ByteArray) {
//        resolveRepository(topic).append(cmdId, bytes)
//    }
//
//    override fun close() {
//        topicsRepository.close()
//        consumersRepository.close()
//        topicRepositoryMapping.keys().forEach { topic ->
//            resolveRepository(topic).close()
//        }
//    }
//
//    override fun consume(consumerId: ConsumerId, topic: DepTopic, limit: Int): List<Chunk> {
//        val nextChunkId = consumersRepository.nextChunkId(consumerId, topic.id)
//        return resolveRepository(topic).read(nextChunkId, limit)
//    }
//
//    override fun commit(consumerId: ConsumerId, topic: DepTopic, chunkId: ChunkId) {
//        consumersRepository.commit(consumerId, topic.id, chunkId)
//    }
//
//    override fun findTopic(topicId: TopicId) = topicsRepository.find(topicId)
//    override fun findTopic(flowId: FlowId, topicName: TopicName) =
//        topicsRepository.find(flowId, topicName)
//
//    override fun listTopics(query: TopicQuery): List<DepTopic> {
//        return topicsRepository.list(query)
//    }
//
//    override fun resolveTopic(flowId: FlowId, name: TopicName) = topicsRepository.find(flowId, name)
//
//    override fun clear() {
//        topicsRepository.clear()
//        consumersRepository.clear()
//        topicRepositoryMapping.keys().forEach { topic ->
//            resolveRepository(topic).clear()
//        }
//    }
//
//
//    override fun read(firstId: ChunkId, topic: DepTopic, limit: Int): List<Chunk> {
//        return resolveRepository(topic).read(firstId, limit)
//    }
//
//    private fun resolveRepository(topic: DepTopic) = topicRepositoryMapping(topic) {
//        TopicSqliteRepository(topic, path = broker.path)
//    }
//}