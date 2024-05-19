package io.hamal.repository.memory.record.namespace_tree

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.api.NamespaceTreeCmdRepository
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import io.hamal.repository.api.NamespaceTreeRepository
import io.hamal.repository.memory.record.RecordMemoryRepository
import io.hamal.repository.record.namespace_tree.CreateNamespaceTreeFromRecords
import io.hamal.repository.record.namespace_tree.NamespaceTreeRecord
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class NamespaceTreeMemoryRepository : RecordMemoryRepository<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree>(
    createDomainObject = CreateNamespaceTreeFromRecords,
    recordClass = NamespaceTreeRecord::class,
    projections = listOf(ProjectionCurrent())
), NamespaceTreeRepository {

    override fun create(cmd: NamespaceTreeCmdRepository.CreateCmd): NamespaceTree {
        return lock.withLock {
            val treeId = cmd.treeId
            if (commandAlreadyApplied(cmd.id, treeId)) {
                versionOf(treeId, cmd.id)
            } else {
                store(
                    NamespaceTreeRecord.Created(
                        cmdId = cmd.id,
                        entityId = treeId,
                        rootId = cmd.rootNodeIndex,
                        workspaceId = cmd.workspaceId,
                    )
                )
                (currentVersion(treeId)).also(currentProjection::upsert)
            }
        }
    }

    override fun append(cmd: NamespaceTreeCmdRepository.AppendCmd): NamespaceTree {
        return lock.withLock {
            val treeId = cmd.treeId
            if (commandAlreadyApplied(cmd.id, treeId)) {
                versionOf(treeId, cmd.id)
            } else {
                store(
                    NamespaceTreeRecord.Appended(
                        cmdId = cmd.id,
                        entityId = treeId,
                        parentId = cmd.parentId,
                        namespaceId = cmd.namespaceId
                    )
                )
                (currentVersion(treeId)).also(currentProjection::upsert)
            }
        }
    }

    override fun close() {}

    override fun find(namespaceId: NamespaceId): NamespaceTree? = lock.withLock { currentProjection.find(namespaceId) }

    override fun list(query: NamespaceTreeQuery): List<NamespaceTree> = lock.withLock { currentProjection.list(query) }

    override fun count(query: NamespaceTreeQuery): Count = lock.withLock { currentProjection.count(query) }

    private val lock = ReentrantLock()
    private val currentProjection = getProjection<ProjectionCurrent>()
}