package io.hamal.repository.sqlite.record.topic

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository.TopicCreateCmd
import io.hamal.repository.api.TopicEvent
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.CreateTopicCmd
import io.hamal.repository.api.log.LogEventId.Companion.LogEventId
import io.hamal.repository.record.serde
import io.hamal.repository.record.topic.CreateTopicFromRecords
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

class TopicSqliteRepository(
    path: Path,
    private val logBrokerRepository: LogBrokerRepository
) : RecordSqliteRepository<TopicId, TopicRecord, Topic>(
    path = path,
    filename = "topic.db",
    createDomainObject = CreateTopicFromRecords,
    recordClass = TopicRecord::class,
    projections = listOf(ProjectionCurrent, ProjectionUniqueName)
), TopicRepository {

    override fun create(cmd: TopicCreateCmd): Topic {
        val topicId = cmd.topicId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, topicId)) {
                versionOf(topicId, cmdId)
            } else {
                store(
                    TopicRecord.Created(
                        cmdId = cmd.id,
                        entityId = topicId,
                        workspaceId = cmd.workspaceId,
                        namespaceId = cmd.namespaceId,
                        logTopicId = cmd.logTopicId,
                        name = cmd.name,
                        type = cmd.type
                    )
                )

                currentVersion(topicId)
                    .also { logBrokerRepository.create(CreateTopicCmd(cmd.id, cmd.logTopicId)) }
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(topicId: TopicId): Topic? = ProjectionCurrent.find(connection, topicId)

    override fun findTopic(namespaceId: NamespaceId, topicName: TopicName): Topic? =
        ProjectionCurrent.find(connection, namespaceId, topicName)

    override fun list(query: TopicQuery): List<Topic> = ProjectionCurrent.list(connection, query)

    override fun list(query: TopicEventQuery): List<TopicEvent> {
        val topic = get(query.topicId)
        return logBrokerRepository.read(
            firstId = LogEventId(SnowflakeId(query.afterId.longValue + 1)),
            topicId = topic.logTopicId,
            limit = query.limit
        ).map { evt ->
            TopicEvent(
                id = TopicEventId(evt.id.value),
                payload = TopicEventPayload(serde.decompressAndRead(ValueObject::class, evt.bytes))
            )
        }
    }

    override fun count(query: TopicQuery): Count = ProjectionCurrent.count(connection, query)

    override fun count(query: TopicEventQuery): Count = TODO()

}

