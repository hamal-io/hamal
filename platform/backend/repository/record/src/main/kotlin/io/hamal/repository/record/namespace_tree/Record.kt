package io.hamal.repository.record.namespace_tree

import io.hamal.lib.common.TreeNode
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class NamespaceTreeRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<NamespaceTreeId>() {
    internal object Adapter : RecordAdapter<NamespaceTreeRecord>(
        listOf(
            Created::class,
            Appended::class
        )
    )

    data class Created(
        override val entityId: NamespaceTreeId,
        override val cmdId: CmdId,
        val workspaceId: WorkspaceId,
        val root: TreeNode<NamespaceId>
    ) : NamespaceTreeRecord()

    data class Appended(
        override val entityId: NamespaceTreeId,
        override val cmdId: CmdId,
        val root: TreeNode<NamespaceId>
    ) : NamespaceTreeRecord()
}
