package io.hamal.repository.record.extension

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class ExtensionRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<ExtensionId>() {
    internal object Adapter : RecordAdapter<ExtensionRecord>(
        listOf(
            Created::class,
            Updated::class
        )
    )

    data class Created(
        override val entityId: ExtensionId,
        override val cmdId: CmdId,
        val workspaceId: WorkspaceId,
        val name: ExtensionName,
        val code: ExtensionCode

    ) : ExtensionRecord()

    data class Updated(
        override val entityId: ExtensionId,
        override val cmdId: CmdId,
        val name: ExtensionName,
        val code: ExtensionCode
    ) : ExtensionRecord()

}
