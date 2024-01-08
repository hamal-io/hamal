package io.hamal.repository.record.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class FuncRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<FuncId>()

data class FuncCreatedRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val flowId: FlowId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeId: CodeId,
    val codeVersion: CodeVersion
) : FuncRecord()

data class FuncUpdatedRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeVersion: CodeVersion
) : FuncRecord()


data class FuncDeployedRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val version: CodeVersion,
    val message: DeployMessage
) : FuncRecord()