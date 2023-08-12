package io.hamal.backend.repository.record.namespace

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
sealed class NamespaceRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<NamespaceId>()

@Serializable
@SerialName("NSCR")
data class NamespaceCreationRecord(
    override val entityId: NamespaceId,
    override val cmdId: CmdId,
    val name: NamespaceName,
    val inputs: NamespaceInputs,
) : NamespaceRecord()

@Serializable
@SerialName("NSUR")
data class NamespaceUpdatedRecord(
    override val entityId: NamespaceId,
    override val cmdId: CmdId,
    val name: NamespaceName,
    val inputs: NamespaceInputs,
) : NamespaceRecord()