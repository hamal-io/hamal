package io.hamal.backend.repository.record.func

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.*
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
    val name: FuncName,
    val inputs: FuncInputs,
    val secrets: FuncSecrets,
    val code: Code
) : FuncRecord()