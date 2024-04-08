package com.nyanbot.repository.impl.flow

import com.nyanbot.repository.Flow
import com.nyanbot.repository.FlowCmdRepository.CreateCmd
import com.nyanbot.repository.FlowId
import com.nyanbot.repository.FlowQueryRepository.FlowQuery
import com.nyanbot.repository.FlowRepository
import com.nyanbot.repository.impl.RecordSqliteRepository
import com.nyanbot.repository.record.CreateDomainObject
import io.hamal.lib.common.domain.Count
import java.nio.file.Path

internal object CreateFlow : CreateDomainObject<FlowId, FlowRecord, Flow> {
    override fun invoke(recs: List<FlowRecord>): Flow {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()

        check(firstRecord is FlowRecord.Created)

        var result = FlowEntity(
            id = firstRecord.entityId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class FlowSqliteRepository(
    path: Path
) : RecordSqliteRepository<FlowId, FlowRecord, Flow>(
    path = path,
    filename = "flow.db",
    createDomainObject = CreateFlow,
    recordClass = FlowRecord::class,
    projections = listOf(ProjectionCurrent)
), FlowRepository {


    override fun create(cmd: CreateCmd): Flow {
        val flowId = cmd.flowId
        return tx {
            store(
                FlowRecord.Created(
                    entityId = flowId,
                )
            )

            currentVersion(flowId).also { ProjectionCurrent.upsert(this, it) }
        }
    }

    override fun find(flowId: FlowId): Flow? {
        return ProjectionCurrent.find(connection, flowId)
    }

    override fun list(query: FlowQuery): List<Flow> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: FlowQuery): Count {
        return ProjectionCurrent.count(connection, query)
    }
}