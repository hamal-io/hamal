package io.hamal.repository.record.workspace

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class GroupRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<GroupId>() {
    internal object Adapter : RecordAdapter<GroupRecord>(
        listOf(
            Created::class
        )
    )

    data class Created(
        override val entityId: GroupId,
        override val cmdId: CmdId,
        val name: GroupName,
        val creatorId: AccountId
    ) : GroupRecord()
}

