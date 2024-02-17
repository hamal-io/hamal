package io.hamal.repository.memory.record.endpoint

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery

internal object EndpointCurrentProjection {
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
