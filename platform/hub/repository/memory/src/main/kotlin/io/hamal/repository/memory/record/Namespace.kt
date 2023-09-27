package io.hamal.repository.memory.record

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceRepository
import io.hamal.repository.record.namespace.CreateNamespaceFromRecords
import io.hamal.repository.record.namespace.NamespaceCreationRecord
import io.hamal.repository.record.namespace.NamespaceRecord
import io.hamal.repository.record.namespace.NamespaceUpdatedRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentNamespaceProjection {
    private val projection = mutableMapOf<NamespaceId, Namespace>()

    fun apply(namespace: Namespace) {
        val currentNamespace = projection[namespace.id]
        projection.remove(namespace.id)

        val namespacesInGroup = projection.values.filter { it.groupId == namespace.groupId }
        if (namespacesInGroup.any { it.name == namespace.name }) {
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
                if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
            }.dropWhile { it.id >= query.afterId }
            .take(query.limit.value)
            .toList()
    }

    fun count(query: NamespaceQuery): ULong {
        return projection.filter { query.namespaceIds.isEmpty() || it.key in query.namespaceIds }
            .map { it.value }
            .reversed()
            .asSequence()
            .filter {
                if (query.groupIds.isEmpty()) true else query.groupIds.contains(it.groupId)
            }.dropWhile { it.id >= query.afterId }
            .count()
            .toULong()
    }

    fun clear() {
        projection.clear()
    }
}

class MemoryNamespaceRepository :
    MemoryRecordRepository<NamespaceId, NamespaceRecord, Namespace>(
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
                    NamespaceCreationRecord(
                        cmdId = cmd.id,
                        entityId = namespaceId,
                        groupId = cmd.groupId,
                        name = cmd.name,
                        inputs = cmd.inputs,
                    )
                )
                (currentVersion(namespaceId)).also(CurrentNamespaceProjection::apply)
            }
        }
    }

    override fun update(namespaceId: NamespaceId, cmd: NamespaceCmdRepository.UpdateCmd): Namespace {
        return lock.withLock {
            if (commandAlreadyApplied(cmd.id, namespaceId)) {
                versionOf(namespaceId, cmd.id)
            } else {
                val current = currentVersion(namespaceId)
                store(
                    NamespaceUpdatedRecord(
                        entityId = namespaceId,
                        cmdId = cmd.id,
                        name = cmd.name ?: current.name,
                        inputs = cmd.inputs ?: current.inputs,
                    )
                )
                (currentVersion(namespaceId)).also(CurrentNamespaceProjection::apply)
            }
        }
    }

    override fun find(namespaceId: NamespaceId): Namespace? = CurrentNamespaceProjection.find(namespaceId)

    override fun find(namespaceName: NamespaceName): Namespace? = CurrentNamespaceProjection.find(namespaceName)

    override fun list(query: NamespaceQuery): List<Namespace> {
        return CurrentNamespaceProjection.list(query)
    }

    override fun count(query: NamespaceQuery): ULong {
        return CurrentNamespaceProjection.count(query)
    }

    override fun clear() {
        super.clear()
        CurrentNamespaceProjection.clear()
    }

    override fun close() {}

    private val lock = ReentrantLock()
}
