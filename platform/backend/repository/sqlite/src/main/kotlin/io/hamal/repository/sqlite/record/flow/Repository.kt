package io.hamal.repository.sqlite.record.flow

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Flow
import io.hamal.repository.api.FlowCmdRepository
import io.hamal.repository.api.FlowCmdRepository.CreateCmd
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import io.hamal.repository.api.FlowRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.flow.FlowCreatedRecord
import io.hamal.repository.record.flow.FlowEntity
import io.hamal.repository.record.flow.FlowRecord
import io.hamal.repository.record.flow.FlowUpdatedRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateFlow : CreateDomainObject<FlowId, FlowRecord, Flow> {
    override fun invoke(recs: List<FlowRecord>): Flow {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is FlowCreatedRecord)

        var result = FlowEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            sequence = firstRecord.sequence(),
            type = firstRecord.type,
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class FlowSqliteRepository(
    config: Config
) : SqliteRecordRepository<FlowId, FlowRecord, Flow>(
    config = config,
    createDomainObject = CreateFlow,
    recordClass = FlowRecord::class,
    projections = listOf(ProjectionCurrent, ProjectionUniqueName)
), FlowRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "flow.db"
    }

    override fun create(cmd: CreateCmd): Flow {
        val flowId = cmd.flowId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, flowId)) {
                versionOf(flowId, cmdId)
            } else {
                store(
                    FlowCreatedRecord(
                        cmdId = cmdId,
                        entityId = flowId,
                        groupId = cmd.groupId,
                        type = cmd.type!!,
                        name = cmd.name,
                        inputs = cmd.inputs,
                    )
                )

                currentVersion(flowId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun update(flowId: FlowId, cmd: FlowCmdRepository.UpdateCmd): Flow {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, flowId)) {
                versionOf(flowId, cmdId)
            } else {
                val current = currentVersion(flowId)
                store(
                    FlowUpdatedRecord(
                        entityId = flowId,
                        cmdId = cmdId,
                        name = cmd.name ?: current.name,
                        inputs = cmd.inputs ?: current.inputs,
                    )
                )
                currentVersion(flowId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(flowId: FlowId): Flow? {
        return ProjectionCurrent.find(connection, flowId)
    }

    override fun find(flowName: FlowName): Flow? {
        return ProjectionUniqueName.find(connection, flowName)?.let { find((it)) }
    }

    override fun list(query: FlowQuery): List<Flow> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: FlowQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}