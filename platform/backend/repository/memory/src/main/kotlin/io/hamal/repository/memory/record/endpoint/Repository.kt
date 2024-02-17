package io.hamal.repository.memory.record.endpoint

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.EndpointRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.endpoint.CreateEndpointFromRecords
import io.hamal.repository.record.endpoint.EndpointRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class EndpointMemoryRepository : RecordMemoryRepository<EndpointId, EndpointRecord, Endpoint>(
    createDomainObject = CreateEndpointFromRecords,
    recordClass = EndpointRecord::class
), EndpointRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: EndpointCmdRepository.CreateCmd): Endpoint {
        return lock.withLock {
            val endpointId = cmd.endpointId
            val cmdId = cmd.id
            if (commandAlreadyApplied(cmdId, endpointId)) {
                versionOf(endpointId, cmd.id)
            } else {
                store(
                    EndpointRecord.Created(
                        cmdId = cmd.id,
                        entityId = endpointId,
                        workspaceId = cmd.workspaceId,
                        namespaceId = cmd.namespaceId,
                        funcId = cmd.funcId,
                        name = cmd.name
                    )
                )
                (currentVersion(endpointId)).also(EndpointCurrentProjection::apply)
            }
        }
    }

    override fun update(endpointId: EndpointId, cmd: EndpointCmdRepository.UpdateCmd): Endpoint {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, endpointId)) {
                versionOf(endpointId, cmd.id)
            } else {
                val currentVersion = versionOf(endpointId, cmd.id)
                store(
                    EndpointRecord.Updated(
                        entityId = endpointId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        funcId = cmd.funcId ?: currentVersion.funcId
                    )
                )
                (currentVersion(endpointId)).also(EndpointCurrentProjection::apply)
            }
        }
    }

    override fun find(endpointId: EndpointId): Endpoint? = lock.withLock { EndpointCurrentProjection.find(endpointId) }

    override fun list(query: EndpointQuery): List<Endpoint> = lock.withLock { EndpointCurrentProjection.list(query) }

    override fun count(query: EndpointQuery): Count = lock.withLock { EndpointCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            EndpointCurrentProjection.clear()
        }
    }

    override fun close() {}
}
