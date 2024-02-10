package io.hamal.repository.record.code

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.GroupId
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class CodeRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<CodeId>() {

    internal object Adapter : RecordAdapter<CodeRecord>(
        listOf(
            Created::class,
            Updated::class
        )
    )

    data class Created(
        override val entityId: CodeId,
        override val cmdId: CmdId,
        val groupId: GroupId,
        val value: CodeValue,
        val type: CodeType
    ) : CodeRecord()

    data class Updated(
        override val entityId: CodeId,
        override val cmdId: CmdId,
        val value: CodeValue
    ) : CodeRecord()
}

