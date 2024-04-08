package com.nyanbot.repository.impl.flow

import com.nyanbot.repository.Flow
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.record.CreateDomainObject
import com.nyanbot.repository.record.RecordEntity
import com.nyanbot.repository.record.RecordSequence
import com.nyanbot.repository.record.RecordedAt


data class FlowEntity(
    override val id: FlowId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,


    ) : RecordEntity<FlowId, FlowRecord, Flow> {

    override fun apply(rec: FlowRecord): FlowEntity {
        return when (rec) {
            is FlowRecord.Created -> copy(
                id = rec.entityId,
                sequence = rec.sequence(),
                recordedAt = rec.recordedAt()
            )
        }
    }


    override fun toDomainObject(): Flow {
        return Flow(
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
        )
    }
}

fun List<FlowRecord>.createEntity(): FlowEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is FlowRecord.Created)

    var result = FlowEntity(
        id = firstRecord.entityId,
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