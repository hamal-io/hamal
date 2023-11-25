package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.EndpointId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointCmdRepository
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.EndpointRepository
import io.hamal.repository.record.endpoint.CreateEndpointFromRecords
import io.hamal.repository.record.endpoint.EndpointCreatedRecord
import io.hamal.repository.record.endpoint.EndpointRecord
import io.hamal.repository.record.endpoint.EndpointUpdatedRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentEndpointProjection {
    private val projection = mutableMapOf<EndpointId, Endpoint>()
    fun apply(endpoint: Endpoint) {
        val currentEndpoint = projection[endpoint.id]
        projection.remove(endpoint.id)

        val endpointsInFlow = projection.values.filter { it.flowId == endpoint.flowId }
        if (endpointsInFlow.any { it.name == endpoint.name }) {
            if (currentEndpoint != null) {
                projection[currentEndpoint.id] = currentEndpoint
            }
            throw IllegalArgumentException("${endpoint.name} already exists in flow ${endpoint.flowId}")
        }

        projection[endpoint.id] = endpoint
    }

    fun find(endpointId: EndpointId): Endpoint? = projection[endpointId]

    fun list(query: EndpointQuery): List<Endpoint> {
        return projection.filter { query.endpointIds.isEmpty() || it.key in query.endpointIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
            .dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: EndpointQuery): ULong {
        return projection.filter { query.endpointIds.isEmpty() || it.key in query.endpointIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter { if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId) }
            .filter { if (query.flowIds.isEmpty()) true else query.flowIds.contains(it.flowId) }
            .dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryEndpointRepository : MemoryRecordRepository<EndpointId, EndpointRecord, Endpoint>(
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
                    EndpointCreatedRecord(
                        cmdId = cmd.id,
                        entityId = endpointId,
                        groupId = cmd.groupId,
                        flowId = cmd.flowId,
                        funcId = cmd.funcId,
                        name = cmd.name
                    )
                )
                (currentVersion(endpointId)).also(CurrentEndpointProjection::apply)
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
                    EndpointUpdatedRecord(
                        entityId = endpointId,
                        cmdId = cmd.id,
                        name = cmd.name ?: currentVersion.name,
                        funcId = cmd.funcId ?: currentVersion.funcId
                    )
                )
                (currentVersion(endpointId)).also(CurrentEndpointProjection::apply)
            }
        }
    }

    override fun find(endpointId: EndpointId): Endpoint? = lock.withLock { CurrentEndpointProjection.find(endpointId) }

    override fun list(query: EndpointQuery): List<Endpoint> = lock.withLock { CurrentEndpointProjection.list(query) }

    override fun count(query: EndpointQuery): ULong = lock.withLock { CurrentEndpointProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            CurrentEndpointProjection.clear()
        }
    }

    override fun close() {}
}
