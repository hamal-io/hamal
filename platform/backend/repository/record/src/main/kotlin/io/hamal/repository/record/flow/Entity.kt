package io.hamal.repository.record.flow

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Flow
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class FlowEntity(
    override val cmdId: CmdId,
    override val id: FlowId,
    override val recordedAt: RecordedAt,
    override val sequence: RecordSequence,
    val groupId: GroupId,
    val type: FlowType,


    var name: FlowName? = null,
    var inputs: FlowInputs? = null,

    ) : RecordEntity<FlowId, FlowRecord, Flow> {

    override fun apply(rec: FlowRecord): FlowEntity {
        return when (rec) {
            is FlowCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                type = rec.type,
                name = rec.name,
                inputs = rec.inputs,
                recordedAt = rec.recordedAt()
            )

            is FlowUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Flow {
        return Flow(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            groupId = groupId,
            type = type,
            name = name!!,
            inputs = inputs!!,
        )
    }
}

fun List<FlowRecord>.createEntity(): FlowEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is FlowCreatedRecord)

    var result = FlowEntity(
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        cmdId = firstRecord.cmdId,
        type = firstRecord.type,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateFlowFromRecords : CreateDomainObject<FlowId, FlowRecord, Flow> {
    override fun invoke(recs: List<FlowRecord>): Flow {
        return recs.createEntity().toDomainObject()
    }
}