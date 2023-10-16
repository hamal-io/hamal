package io.hamal.repository.record.func

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.FuncCode
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
sealed class FuncRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<FuncId>()

@Serializable
@SerialName("FCR")
data class FuncCreationRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: FuncCode
) : FuncRecord()

@Serializable
@SerialName("FUR")
data class FuncUpdatedRecord(
    override val entityId: FuncId,
    override val cmdId: CmdId,
    val namespaceId: NamespaceId,
    val name: FuncName,
    val inputs: FuncInputs,
    val code: FuncCode
) : FuncRecord()