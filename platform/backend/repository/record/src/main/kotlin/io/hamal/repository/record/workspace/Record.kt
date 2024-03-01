package io.hamal.repository.record.workspace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class WorkspaceRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<WorkspaceId>() {
    internal object Adapter : RecordAdapter<WorkspaceRecord>(
        listOf(
            Created::class
        )
    )

    data class Created(
        override val entityId: WorkspaceId,
        override val cmdId: CmdId,
        val name: WorkspaceName,
        val creatorId: AccountId
    ) : WorkspaceRecord()
}

