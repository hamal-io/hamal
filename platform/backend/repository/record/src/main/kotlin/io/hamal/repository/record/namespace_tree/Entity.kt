package io.hamal.repository.record.namespace_tree

import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.TreeNodeMutable
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.mutate
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.NamespaceTree
import io.hamal.repository.record.CreateDomainObject
import io.hamal.repository.record.RecordEntity
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

data class NamespaceTreeEntity(
    override val cmdId: CmdId,
    override val id: NamespaceTreeId,
    override val recordedAt: RecordedAt,
    override val sequence: RecordSequence,
    val workspaceId: WorkspaceId,
    var root: TreeNode<NamespaceId>? = null
) : RecordEntity<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree> {

    override fun apply(rec: NamespaceTreeRecord): NamespaceTreeEntity {
        return when (rec) {
            is NamespaceTreeRecord.Created -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                workspaceId = rec.workspaceId,
                root = TreeNode(rec.rootId),
                recordedAt = rec.recordedAt()
            )

            is NamespaceTreeRecord.Appended ->
                copy(
                    id = rec.entityId,
                    cmdId = rec.cmdId,
                    sequence = rec.sequence(),
                    root = root!!.mutate().let { rootNode ->
                        val parent = rootNode.find { it.value == rec.parentId }!!
                        parent.descendants.add(TreeNodeMutable(rec.namespaceId))
                        rootNode.toTreeNode()
                    },
                    recordedAt = rec.recordedAt()
                )

            is NamespaceTreeRecord.Reduced -> copy(
                id = rec.entityId,
                cmdId = rec.cmdId,
                sequence = rec.sequence(),
                root = root!!.mutate().let { rootNode ->
                    val parent = rootNode.find { it.value == rec.parentId }!!
                    parent.descendants.clear()
                    rootNode.toTreeNode()
                },
                recordedAt = rec.recordedAt()
            )

        }
    }

    override fun toDomainObject(): NamespaceTree {
        return NamespaceTree(
            cmdId = cmdId,
            id = id,
            updatedAt = recordedAt.toUpdatedAt(),
            workspaceId = workspaceId,
            root = root!!
        )
    }
}

fun List<NamespaceTreeRecord>.createEntity(): NamespaceTreeEntity {
    check(isNotEmpty()) { "At least one record is required" }
    val firstRecord = first()
    check(firstRecord is NamespaceTreeRecord.Created)

    var result = NamespaceTreeEntity(
        id = firstRecord.entityId,
        cmdId = firstRecord.cmdId,
        sequence = firstRecord.sequence(),
        recordedAt = firstRecord.recordedAt(),
        workspaceId = firstRecord.workspaceId
    )

    forEach { record ->
        result = result.apply(record)
    }

    return result
}


object CreateNamespaceTreeFromRecords : CreateDomainObject<NamespaceTreeId, NamespaceTreeRecord, NamespaceTree> {
    override fun invoke(recs: List<NamespaceTreeRecord>): NamespaceTree {
        return recs.createEntity().toDomainObject()
    }
}