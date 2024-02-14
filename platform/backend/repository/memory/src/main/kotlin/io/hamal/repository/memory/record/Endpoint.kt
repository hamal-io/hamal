package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.EndpointRepository
import io.hamal.repository.record.endpoint.CreateEndpointFromRecords
import io.hamal.repository.record.endpoint.EndpointRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object EndpointCurrentProjection {
    private val projection = mutableMapOf<EndpointId, Endpoint>()
    fun apply(endpoint: Endpoint) {
        val currentEndpoint = projection[endpoint.id]
        projection.remove(endpoint.id)

        val endpointsInNamespace = projection.values.filter { it.namespaceId == endpoint.namespaceId }
        if (endpointsInNamespace.any { it.name == endpoint.name }) {
            if (currentEndpoint != null) {
                projection[currentEndpoint.id] = currentEndpoint
            }
            throw IllegalArgumentException("${endpoint.name} already exists in namespace ${endpoint.namespaceId}")
        }

        projection[endpoint.id] = endpoint
    }

    fun find(endpointId: EndpointId): Endpoint? = projection[endpointId]

    fun list(query: EndpointQuery): List<Endpoint> {
        return projection.filter { query.endpointIds.isEmpty() || it.key in query.endpointIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
            .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: EndpointQuery): Count {
        return Count(
            projection.filter { query.endpointIds.isEmpty() || it.key in query.endpointIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter { if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId) }
                .filter { if (query.namespaceIds.isEmpty()) true else query.namespaceIds.contains(it.namespaceId) }
                .dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

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
