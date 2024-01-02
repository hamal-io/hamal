package io.hamal.repository.record.endpoint

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.Transient

sealed class EndpointRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    override var recordedAt: RecordedAt? = null
) : Record<EndpointId>()

data class EndpointCreatedRecord(
    override val entityId: EndpointId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val flowId: FlowId,
    val funcId: FuncId,
    val name: EndpointName
) : EndpointRecord()

data class EndpointUpdatedRecord(
    override val entityId: EndpointId,
    override val cmdId: CmdId,
    val funcId: FuncId,
    val name: EndpointName
) : EndpointRecord()