package io.hamal.repository.record.endpoint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Endpoint
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class EndpointEntity(
    override val cmdId: CmdId,
    override val id: EndpointId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,
    val groupId: GroupId,

    var flowId: FlowId? = null,
    var funcId: FuncId? = null,
    var name: EndpointName? = null,

    ) : RecordEntity<EndpointId, EndpointRecord, Endpoint> {

    override fun apply(rec: EndpointRecord): EndpointEntity {
        return when (rec) {
            is EndpointCreatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                flowId = rec.flowId,
                name = rec.name,
                funcId = rec.funcId,
                recordedAt = rec.recordedAt()

            )

            is EndpointUpdatedRecord -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                name = rec.name,
                funcId = rec.funcId,
                recordedAt = rec.recordedAt()
            )
        }
    }

    override fun toDomainObject(): Endpoint {
        return Endpoint(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            groupId = groupId,
            flowId = flowId!!,
            funcId = funcId!!,
            name = name!!
        )
    }
}

fun List<EndpointRecord>.createEntity(): EndpointEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is EndpointCreatedRecord)

    var result = EndpointEntity(
        id = firstRecord.entityId,
        groupId = firstRecord.groupId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt()
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}

object CreateEndpointFromRecords : CreateDomainObject<EndpointId, EndpointRecord, Endpoint> {
    override fun invoke(recs: List<EndpointRecord>): Endpoint {
        return recs.createEntity().toDomainObject()
    }
}