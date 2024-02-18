package io.hamal.repository.sqlite.record.namespace_tree

import io.hamal.lib.common.domain.Count
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.api.NamespaceTreeCmdRepository
import io.hamal.repository.api.NamespaceTreeCmdRepository.CreateCmd
import io.hamal.repository.api.NamespaceTreeQueryRepository.NamespaceTreeQuery
import io.hamal.repository.api.NamespaceTreeRepository
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.namespace_tree.NamespaceTreeEntity
import io.hamal.repository.record.namespace_tree.NamespaceTreeRecord
import io.hamal.repository.sqlite.record.RecordSqliteRepository
import java.nio.file.Path

internal object CreateNamespaceTree : CreateDomainObject<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree> {
    override fun invoke(recs: List<NamespaceTreeRecord>): NamespaceTree {
        check(recs.isNotEmpty()) { "At least one record is required" }
        val firstRecord = recs.first()
        check(firstRecord is NamespaceTreeRecord.Created)

        var result = NamespaceTreeEntity(
            cmdId = firstRecord.cmdId,
            id = firstRecord.entityId,
            workspaceId = firstRecord.workspaceId,
            sequence = firstRecord.sequence(),
            recordedAt = firstRecord.recordedAt()
        )

        recs.forEach { record ->
            result = result.apply(record)
        }

        return result.toDomainObject()
    }
}

class NamespaceTreeSqliteRepository(
    path: Path
) : RecordSqliteRepository<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree>(
    path = path,
    filename = "namespace-tree.db",
    createDomainObject = CreateNamespaceTree,
    recordClass = NamespaceTreeRecord::class,
    projections = listOf(ProjectionCurrent)
), NamespaceTreeRepository {

    override fun create(cmd: CreateCmd): NamespaceTree {
        return tx {
            val treeId = cmd.treeId
            if (commandAlreadyApplied(cmd.id, treeId)) {
                versionOf(treeId, cmd.id)
            } else {
                store(
                    NamespaceTreeRecord.Created(
                        cmdId = cmd.id,
                        entityId = treeId,
                        rootId = cmd.rootNodeId,
                        workspaceId = cmd.workspaceId,
                    )
                )
                currentVersion(treeId).also { ProjectionCurrent.upsert(this, it) }
            }
        }
    }

    override fun append(cmd: NamespaceTreeCmdRepository.AppendCmd): NamespaceTree {
        return tx {
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
                (currentVersion(treeId)).also {
                    ProjectionCurrent.upsert(this, it)
                }
            }
        }
    }

    override fun find(namespaceId: NamespaceId): NamespaceTree? = ProjectionCurrent.find(connection, namespaceId)

    override fun list(query: NamespaceTreeQuery): List<NamespaceTree> =
        ProjectionCurrent.list(connection, query)


    override fun count(query: NamespaceTreeQuery): Count =
        ProjectionCurrent.count(connection, query)
}