package io.hamal.repository.memory.record

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceRepository
import io.hamal.repository.record.namespace.NamespaceCreationRecord
import io.hamal.repository.record.namespace.NamespaceRecord
import io.hamal.repository.record.namespace.NamespaceUpdatedRecord
import io.hamal.repository.record.namespace.createEntity
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

class MemoryNamespaceRepository : BaseRecordRepository<NamespaceId, NamespaceRecord>(), NamespaceRepository {

    override fun create(cmd: CreateCmd): Namespace {
        return lock.withLock {
            val namespaceId = cmd.namespaceId
            if (contains(namespaceId)) {
                versionOf(namespaceId, cmd.id)
            } else {
                addRecord(
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
            if (commandAlreadyApplied(namespaceId, cmd.id)) {
                versionOf(namespaceId, cmd.id)
            } else {
                val current = currentVersion(namespaceId)
                addRecord(
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

private fun MemoryNamespaceRepository.currentVersion(id: NamespaceId): Namespace {
    return listRecords(id)
        .createEntity()
        .toDomainObject()
}

private fun MemoryNamespaceRepository.commandAlreadyApplied(id: NamespaceId, cmdId: CmdId) =
    listRecords(id).any { it.cmdId == cmdId }

private fun MemoryNamespaceRepository.versionOf(id: NamespaceId, cmdId: CmdId): Namespace {
    return listRecords(id).takeWhileInclusive { it.cmdId != cmdId }
        .createEntity()
        .toDomainObject()
}