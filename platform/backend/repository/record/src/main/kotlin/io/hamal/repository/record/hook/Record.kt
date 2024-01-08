package io.hamal.repository.record.hook

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookName
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class HookRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    override var recordedAt: RecordedAt? = null
) : Record<HookId>()

data class HookCreatedRecord(
    override val entityId: HookId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val flowId: FlowId,
    val name: HookName
) : HookRecord()

data class HookUpdatedRecord(
    override val entityId: HookId,
    override val cmdId: CmdId,
    val name: HookName
) : HookRecord()