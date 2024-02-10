package io.hamal.repository.sqlite.record.topic

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Topic
import io.hamal.repository.api.TopicCmdRepository
import io.hamal.repository.api.TopicCmdRepository.TopicGroupCreateCmd
import io.hamal.repository.api.TopicEvent
import io.hamal.repository.api.TopicQueryRepository.TopicEventQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.TopicRepository
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.LogTopicToCreate
import io.hamal.repository.record.topic.CreateTopicFromRecords
import io.hamal.repository.record.topic.TopicRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

class TopicSqliteRepository(
    config: Config,
    private val logBrokerRepository: LogBrokerRepository
) : RecordSqliteRepository<TopicId, TopicRecord, Topic>(
    config = config,
    createDomainObject = CreateTopicFromRecords,
    recordClass = TopicRecord::class,
    projections = listOf(ProjectionCurrent, ProjectionUniqueName)
), TopicRepository {

    data class Config(
        override val path: Path,
    ) : SqliteBaseRepository.Config {
        override val filename = "topic.db"
    }

    override fun create(cmd: TopicGroupCreateCmd): Topic.Group {
        val topicId = cmd.topicId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, topicId)) {
                versionOf(topicId, cmdId) as Topic.Group
            } else {
                store(
                    TopicRecord.GroupCreated(
                        cmdId = cmd.id,
                        entityId = topicId,
                        groupId = cmd.groupId,
                        logTopicId = cmd.logTopicId,
                        name = cmd.name,
                    )
                )

                (currentVersion(topicId) as Topic.Group)
                    .also { logBrokerRepository.create(cmd.id, LogTopicToCreate(cmd.logTopicId)) }
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun create(cmd: TopicCmdRepository.TopicInternalCreateCmd): Topic.Internal {
        val topicId = cmd.topicId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, topicId)) {
                versionOf(topicId, cmdId) as Topic.Internal
            } else {
                store(
                    TopicRecord.InternalCreated(
                        cmdId = cmd.id,
                        entityId = topicId,
                        logTopicId = cmd.logTopicId,
                        name = cmd.name,
                        groupId = GroupId.root
                    )
                )

                (currentVersion(topicId) as Topic.Internal)
                    .also { logBrokerRepository.create(cmd.id, LogTopicToCreate(cmd.logTopicId)) }
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }


    override fun find(topicId: TopicId): Topic? = ProjectionCurrent.find(connection, topicId)

    override fun findGroupTopic(groupId: GroupId, topicName: TopicName): Topic? =
        ProjectionCurrent.find(connection, groupId, topicName)

    override fun list(query: TopicQuery): List<Topic> = ProjectionCurrent.list(connection, query)

    override fun list(query: TopicEventQuery): List<TopicEvent> {
        TODO()
    }

    override fun count(query: TopicQuery): Count = ProjectionCurrent.count(connection, query)

    override fun count(query: TopicEventQuery): Count = TODO()

}

