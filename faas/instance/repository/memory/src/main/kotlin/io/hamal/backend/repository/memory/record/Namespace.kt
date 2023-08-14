package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.Namespace
import io.hamal.backend.repository.api.NamespaceCmdRepository
import io.hamal.backend.repository.api.NamespaceQueryRepository
import io.hamal.backend.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.backend.repository.record.namespace.NamespaceCreationRecord
import io.hamal.backend.repository.record.namespace.NamespaceRecord
import io.hamal.backend.repository.record.namespace.NamespaceUpdatedRecord
import io.hamal.backend.repository.record.namespace.createEntity
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.CollectionUtils.takeWhileInclusive
import io.hamal.lib.domain.vo.NamespaceId
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal object CurrentNamespaceProjection {
    private val projection = mutableMapOf<NamespaceId, Namespace>()

    fun apply(namespace: Namespace) {
        projection[namespace.id] = namespace
    }

    fun find(namespaceId: NamespaceId): Namespace? = projection[namespaceId]

    fun list(afterId: NamespaceId, limit: Limit): List<Namespace> {
        return projection.keys.sorted()
            .reversed()
            .dropWhile { it >= afterId }
            .take(limit.value)
            .mapNotNull { find(it) }
    }

    fun clear() {
        projection.clear()
    }
}

object MemoryNamespaceRepository : BaseRecordRepository<NamespaceId, NamespaceRecord>(), NamespaceCmdRepository,
    NamespaceQueryRepository {
    private val lock = ReentrantLock()
    override fun create(cmd: NamespaceCmdRepository.CreateCmd): Namespace {
        return lock.withLock {
            val namespaceId = cmd.namespaceId
            if (contains(namespaceId)) {
                versionOf(namespaceId, cmd.id)
            } else {
                addRecord(
                    NamespaceCreationRecord(
                        entityId = namespaceId,
                        cmdId = cmd.id,
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
                addRecord(
                    NamespaceUpdatedRecord(
                        entityId = namespaceId,
                        cmdId = cmd.id,
                        name = cmd.name,
                        inputs = cmd.inputs,
                    )
                )
                (currentVersion(namespaceId)).also(CurrentNamespaceProjection::apply)
            }
        }
    }

    override fun find(namespaceId: NamespaceId): Namespace? = CurrentNamespaceProjection.find(namespaceId)

    override fun list(block: NamespaceQuery.() -> Unit): List<Namespace> {
        val query = NamespaceQuery().also(block)
        return CurrentNamespaceProjection.list(query.afterId, query.limit)
    }

    override fun clear() {
        super.clear()
        CurrentNamespaceProjection.clear()
    }
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