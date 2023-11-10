package io.hamal.repository.record.flow

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.repository.api.Flow
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence

data class FlowEntity(
    override val cmdId: CmdId,
    override val id: FlowId,
    val groupId: GroupId,
    override val sequence: RecordSequence,

    var name: FlowName? = null,
    var inputs: FlowInputs? = null,

    ) : RecordEntity<FlowId, FlowRecord, Flow> {

    override fun apply(rec: FlowRecord): FlowEntity {
        return when (rec) {
            is FlowCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
            )

            is FlowUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                inputs = rec.inputs,
            )
        }
    }

    override fun toDomainObject(): Flow {
        return Flow(
            cmdId = cmdId,
            id = id,
            groupId = groupId,
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
        sequence = firstRecord.sequence()
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