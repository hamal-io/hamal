package io.hamal.repository.record.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class HookRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    override var recordedAt: RecordedAt? = null
) : Record<HookId>() {
    internal object Adapter : RecordAdapter<HookRecord>(
        listOf(
            Created::class,
            Updated::class
        )
    )

    data class Created(
        override val entityId: HookId,
        override val cmdId: CmdId,
        val workspaceId: WorkspaceId,
        val namespaceId: NamespaceId,
        val name: HookName
    ) : HookRecord()

    data class Updated(
        override val entityId: HookId,
        override val cmdId: CmdId,
        val name: HookName
    ) : HookRecord()
}

