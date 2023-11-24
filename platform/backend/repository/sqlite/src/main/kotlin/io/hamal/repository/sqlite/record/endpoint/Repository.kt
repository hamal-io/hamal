package io.hamal.repository.sqlite.record.endpoint

import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository.CreateCmd
import io.hamal.repository.api.EndpointCmdRepository.UpdateCmd
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.EndpointRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.endpoint.EndpointCreatedRecord
import io.hamal.repository.record.endpoint.EndpointEntity
import io.hamal.repository.record.endpoint.EndpointRecord
import io.hamal.repository.record.endpoint.EndpointUpdatedRecord
import io.hamal.repository.sqlite.record.SqliteRecordRepository
import java.nio.file.Path

internal object CreateEndpoint : CreateDomainObject<EndpointId, EndpointRecord, Endpoint> {
    override fun invoke(recs: List<EndpointRecord>): Endpoint {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is EndpointCreatedRecord)

        var result = EndpointEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            groupId = firstRecord.groupId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class SqliteEndpointRepository(
    config: Config
) : SqliteRecordRepository<EndpointId, EndpointRecord, Endpoint>(
    config = config,
    createDomainObject = CreateEndpoint,
    recordClass = EndpointRecord::class,
    projections = listOf(
        ProjectionCurrent,
        ProjectionUniqueName
    )
), EndpointRepository {

    data class Config(
        override val path: Path
    ) : SqliteBaseRepository.Config {
        override val filename = "endpoint.db"
    }

    override fun create(cmd: CreateCmd): Endpoint {
        val endpointId = cmd.endpointId
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, endpointId)) {
                versionOf(endpointId, cmdId)
            } else {
                store(
                    EndpointCreatedRecord(
                        cmdId = cmdId,
                        entityId = endpointId,
                        groupId = cmd.groupId,
                        flowId = cmd.flowId,
                        name = cmd.name
                    )
                )

                currentVersion(endpointId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun update(endpointId: EndpointId, cmd: UpdateCmd): Endpoint {
        val cmdId = cmd.id
        return tx {
            if (commandAlreadyApplied(cmdId, endpointId)) {
                versionOf(endpointId, cmdId)
            } else {
                val currentVersion = versionOf(endpointId, cmdId)
                store(
                    EndpointUpdatedRecord(
                        entityId = endpointId,
                        cmdId = cmdId,
                        name = cmd.name ?: currentVersion.name
                    )
                )
                currentVersion(endpointId)
                    .also { ProjectionCurrent.upsert(this, it) }
                    .also { ProjectionUniqueName.upsert(this, it) }
            }
        }
    }

    override fun find(endpointId: EndpointId): Endpoint? {
        return ProjectionCurrent.find(connection, endpointId)
    }

    override fun list(query: EndpointQuery): List<Endpoint> {
        return ProjectionCurrent.list(connection, query)
    }

    override fun count(query: EndpointQuery): ULong {
        return ProjectionCurrent.count(connection, query)
    }
}