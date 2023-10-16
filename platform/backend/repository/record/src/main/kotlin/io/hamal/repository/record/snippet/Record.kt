package io.hamal.repository.record.snippet

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SnippetRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<SnippetId>()

@Serializable
@SerialName("SCR")
data class SnippetCreationRecord(
    override val entityId: SnippetId,
    override val cmdId: CmdId,
    val groupId: GroupId,
    val name: SnippetName,
    val inputs: SnippetInputs,
    val value: CodeValue,
    val accountId: AccountId
) : SnippetRecord()


@Serializable
@SerialName("SUR")
data class SnippetUpdatedRecord(
    override val entityId: SnippetId,
    override val cmdId: CmdId,
    val name: SnippetName,
    val inputs: SnippetInputs,
    val value: CodeValue
) : SnippetRecord()