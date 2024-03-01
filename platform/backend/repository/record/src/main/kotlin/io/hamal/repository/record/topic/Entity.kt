package io.hamal.repository.record.topic

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.TopicType
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Topic
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class TopicEntity(
    override val id: TopicId,
    override val cmdId: CmdId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,

    var topicId: TopicId? = null,
    var name: TopicName? = null,
    var type: TopicType? = null,
    var logTopicId: LogTopicId? = null,
    var workspaceId: WorkspaceId? = null,
    var namespaceId: NamespaceId? = null

) : RecordEntity<TopicId, TopicRecord, Topic> {

    override fun apply(rec: TopicRecord): TopicEntity {
        return when (rec) {
            is TopicRecord.Created -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                sequence = rec.sequence(),
                name = rec.name,
                workspaceId = rec.workspaceId,
                namespaceId = rec.namespaceId,
                logTopicId = rec.logTopicId,
                type = rec.type,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Topic {
        return when (type!!) {
            TopicType.Internal -> Topic.Internal(
                cmdId = cmdId,
                id = id,
                name = name!!,
                logTopicId = logTopicId!!,
                updatedAt = recordedAt.toUpdatedAt(),
                workspaceId = workspaceId!!,
                namespaceId = namespaceId!!
            )

            TopicType.Namespace -> Topic.Namespace(
                cmdId = cmdId,
                id = id,
                name = name!!,
                logTopicId = logTopicId!!,
                updatedAt = recordedAt.toUpdatedAt(),
                workspaceId = workspaceId!!,
                namespaceId = namespaceId!!
            )

            TopicType.Workspace -> Topic.Workspace(
                cmdId = cmdId,
                id = id,
                name = name!!,
                logTopicId = logTopicId!!,
                updatedAt = recordedAt.toUpdatedAt(),
                workspaceId = workspaceId!!,
                namespaceId = namespaceId!!
            )

            TopicType.Public -> Topic.Public(
                cmdId = cmdId,
                id = id,
                name = name!!,
                logTopicId = logTopicId!!,
                updatedAt = recordedAt.toUpdatedAt(),
                workspaceId = workspaceId!!,
                namespaceId = namespaceId!!
            )

        }
    }
}

fun List<TopicRecord>.createEntity(): TopicEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord: TopicRecord = first()

    check(firstRecord is TopicRecord.Created)

    var result = TopicEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateTopicFromRecords : CreateDomainObject<TopicId, TopicRecord, Topic> {
    override fun invoke(recs: List<TopicRecord>): Topic {
        return recs.createEntity().toDomainObject()
    }
}