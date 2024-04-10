package com.nyanbot.repository.impl.flow

import com.nyanbot.repository.*
import com.nyanbot.repository.record.CreateDomainObject
import com.nyanbot.repository.record.RecordEntity
import com.nyanbot.repository.record.RecordSequence
import com.nyanbot.repository.record.RecordedAt
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TriggerId


data class FlowEntity(
    override val id: FlowId,
    override val sequence: RecordSequence,
    override val recordedAt: RecordedAt,


    private val status: FlowStatus? = null,

    private var accountId: AccountId? = null,
    private var name: FlowName? = null,
    private var flowTrigger: FlowTrigger? = null,

    private var namespaceId: NamespaceId? = null,
    private var funcId: FuncId? = null,
    private var triggerId: TriggerId? = null,

    ) : RecordEntity<FlowId, FlowRecord, Flow> {

    override fun apply(rec: FlowRecord): FlowEntity {
        return when (rec) {
            is FlowRecord.Created -> copy(
                id = rec.entityId,
                sequence = rec.sequence(),
                recordedAt = rec.recordedAt(),
                accountId = rec.accountId,
                status = FlowStatus.Inactive,
                name = rec.name,
                flowTrigger = rec.flowTrigger,
                namespaceId = rec.namespaceId,
                funcId = rec.funcId,
                triggerId = rec.triggerId
            )

            is FlowRecord.SetActive -> copy(
                id = rec.entityId,
                status = FlowStatus.Active
            )

            is FlowRecord.SetInactive -> copy(
                id = rec.entityId,
                status = FlowStatus.Inactive
            )
        }
    }


    override fun toDomainObject(): Flow {
        return Flow(
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            accountId = accountId!!,
            status = status!!,
            name = name!!,
            flowTrigger = flowTrigger!!,

            namespaceId = namespaceId,
            funcId = funcId,
            triggerId = triggerId
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