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

    var groupId: GroupId? = null,
    var flowId: FlowId? = null,
    var funcId: FuncId? = null,

    ) : RecordEntity<TopicId, TopicRecord, Topic> {

    override fun apply(rec: TopicRecord): TopicEntity {
        return when (rec) {
            is TopicFlowCreatedRecord -> copy(
                cmdId = rec.cmdId,
                id = rec.entityId,
                sequence = rec.sequence(),
                name = rec.name,
                groupId = rec.groupId,
                flowId = rec.flowId,
                logTopicId = rec.logTopicId,
                funcId = null,
                type = TopicType.Flow,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Topic {
        return when (type!!) {
            TopicType.Internal -> TODO()
            TopicType.Func -> TODO()
            TopicType.Flow -> Topic.Flow(
                cmdId = cmdId,
                id = id,
                name = name!!,
                logTopicId = logTopicId!!,
                updatedAt = recordedAt.toUpdatedAt(),
                groupId = groupId!!,
                flowId = flowId!!,

                )

            TopicType.Group -> TODO()
            TopicType.Public -> TODO()
        }
    }
}

fun List<TopicRecord>.createEntity(): TopicEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord: TopicRecord = first()

    check(
        firstRecord is TopicFlowCreatedRecord
    )

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