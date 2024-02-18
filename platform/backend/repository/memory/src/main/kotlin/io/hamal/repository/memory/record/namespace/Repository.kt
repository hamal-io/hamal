package io.hamal.repository.memory.record.namespace

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.api.Namespace
import io.hamal.repository.api.NamespaceCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceCmdRepository.UpdateCmd
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.NamespaceRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.namespace.CreateNamespaceFromRecords
import io.hamal.repository.record.namespace.NamespaceRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

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
