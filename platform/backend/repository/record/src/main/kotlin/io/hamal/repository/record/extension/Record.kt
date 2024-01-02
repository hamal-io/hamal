package io.hamal.repository.record.extension

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.RecordedAt
import io.hamal.repository.api.ExtensionCode
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence

sealed class ExtensionRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<ExtensionId>()

data class ExtensionCreatedRecord(
    override val entityId: ExtensionId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: ExtensionName,
    val code: ExtensionCode

) : ExtensionRecord()

data class ExtensionUpdatedRecord(
    override val entityId: ExtensionId,
    override val cmdId: CmdId,
    val name: ExtensionName,
    val code: ExtensionCode
) : ExtensionRecord()
