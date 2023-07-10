package io.hamal.backend.repository.record.trigger

import io.hamal.backend.repository.record.Record
import io.hamal.backend.repository.record.RecordSequence
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.*
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
@SerialName("FRCR")
data class FixedRateTriggerCreationRecord(
    override val entityId: TriggerId,
    override val cmdId: CmdId,
    val funcId: FuncId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val duration: Duration
) : TriggerRecord()

@Serializable
@SerialName("ECR")
data class EventTriggerCreationRecord(
    override val entityId: TriggerId,
    override val cmdId: CmdId,
    val funcId: FuncId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val topicId: TopicId
) : TriggerRecord()