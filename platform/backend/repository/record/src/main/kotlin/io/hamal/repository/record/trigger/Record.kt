package io.hamal.repository.record.trigger

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration


@Serializable
sealed class TriggerRecord(
    @Transient
    override var sequence: RecordSequence? = null
) : Record<TriggerId>()

@Serializable
@SerialName("FRTCR")
data class FixedRateTriggerCreationRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val namespaceId: NamespaceId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val duration: Duration,
    val correlationId: CorrelationId? = null
) : TriggerRecord()

@Serializable
@SerialName("ETCR")
data class EventTriggerCreationRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val namespaceId: NamespaceId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val topicId: TopicId,
    val correlationId: CorrelationId? = null
) : TriggerRecord()

@Serializable
@SerialName("HTCR")
data class HookTriggerCreationRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val namespaceId: NamespaceId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val hookId: HookId,
    val correlationId: CorrelationId? = null
) : TriggerRecord()