package io.hamal.repository.record.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
sealed class FuncRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    override var recordedAt: RecordedAt? = null
) : Record<FuncId>()

@Serializable
@SerialName("FuncCreatedRecord")
data class FuncCreatedRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeId: CodeId,
    val codeVersion: CodeVersion
) : FuncRecord()

@Serializable
@SerialName("FuncUpdatedRecord")
data class FuncUpdatedRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val name: FuncName,
    val inputs: FuncInputs,
    val codeVersion: CodeVersion
) : FuncRecord()


@Serializable
@SerialName("FuncDeployedRecord")
data class FuncDeployedRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val deployedVersion: CodeVersion
) : FuncRecord()