package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceCmdRepository.UpdateCmd
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceRepository
import io.hamal.repository.record.namespace.CreateNamespaceFromRecords
import io.hamal.repository.record.namespace.NamespaceRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object NamespaceCurrentProjection {
    private val projection = mutableMapOf<NamespaceId, Namespace>()

    fun apply(namespace: Namespace) {
        val currentNamespace = projection[namespace.id]
        projection.remove(namespace.id)

        val namespacesInWorkspace = projection.values.filter { it.workspaceId == namespace.workspaceId }
        if (namespacesInWorkspace.any { it.name == namespace.name }) {
            if (currentNamespace != null) {
                projection[currentNamespace.id] = currentNamespace
            }
            throw IllegalArgumentException("${namespace.name} already exists")
        }

        projection[namespace.id] = namespace
    }

    fun find(namespaceId: NamespaceId): Namespace? = projection[namespaceId]
    fun find(namespaceName: NamespaceName): Namespace? = projection.values.find { it.name == namespaceName }

    fun list(query: NamespaceQuery): List<Namespace> {
        return projection.filter { query.namespaceIds.isEmpty() || it.key in query.namespaceIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: NamespaceQuery): Count {
        return Count(
            projection.filter { query.namespaceIds.isEmpty() || it.key in query.namespaceIds }
                .map { it.value }
                .reversed()
                .asSequence()
                .filter {
                    if (query.workspaceIds.isEmpty()) true else query.workspaceIds.contains(it.workspaceId)
                }.dropWhile { it.id >= query.afterId }
                .count()
                .toLong()
        )
    }

    fun clear() {
        projection.clear()
    }
}

class NamespaceMemoryRepository : RecordMemoryRepository<NamespaceId, NamespaceRecord, Namespace>(
    createDomainObject = CreateNamespaceFromRecords,
    recordClass = NamespaceRecord::class
), NamespaceRepository {

    override fun create(cmd: CreateCmd): Namespace {
        return lock.withLock {
            val namespaceId = cmd.namespaceId
            if (commandAlreadyApplied(cmd.id, namespaceId)) {
                versionOf(namespaceId, cmd.id)
            } else {
                store(
                    NamespaceRecord.Created(
                        cmdId = cmd.id,
                        entityId = namespaceId,
                        workspaceId = cmd.workspaceId,
                        name = cmd.name
                    )
                )
                (currentVersion(namespaceId)).also(NamespaceCurrentProjection::apply)
            }
        }
    }

    override fun update(namespaceId: NamespaceId, cmd: UpdateCmd): Namespace {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, namespaceId)) {
                versionOf(namespaceId, cmd.id)
            } else {
                val current = currentVersion(namespaceId)
                store(
                    NamespaceRecord.Updated(
                        entityId = namespaceId,
                        cmdId = cmd.id,
                        name = cmd.name ?: current.name
                    )
                )
                (currentVersion(namespaceId)).also(NamespaceCurrentProjection::apply)
            }
        }
    }

    override fun find(namespaceId: NamespaceId): Namespace? =
        lock.withLock { NamespaceCurrentProjection.find(namespaceId) }

    override fun find(namespaceName: NamespaceName): Namespace? =
        lock.withLock { NamespaceCurrentProjection.find(namespaceName) }

    override fun list(query: NamespaceQuery): List<Namespace> = lock.withLock { NamespaceCurrentProjection.list(query) }

    override fun count(query: NamespaceQuery): Count = lock.withLock { NamespaceCurrentProjection.count(query) }

    override fun clear() {
        lock.withLock {
            super.clear()
            NamespaceCurrentProjection.clear()
        }
    }

    override fun close() {}

    private val lock = ReentrantLock()
}
