package io.hamal.repository.record.trigger

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordSequence
import kotlin.time.Duration

sealed class TriggerRecord(
    @Transient
    override var sequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<TriggerId>()

data class FixedRateTriggerCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val flowId: FlowId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val duration: Duration,
    val status: TriggerStatus,
    val correlationId: CorrelationId? = null
) : TriggerRecord()

data class EventTriggerCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val flowId: FlowId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val topicId: TopicId,
    val status: TriggerStatus,
    val correlationId: CorrelationId? = null
) : TriggerRecord()

data class HookTriggerCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val flowId: FlowId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val hookId: HookId,
    val hookMethod: HookMethod,
    val status: TriggerStatus,
    val correlationId: CorrelationId? = null
) : TriggerRecord()

data class CronTriggerCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val flowId: FlowId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val cron: CronPattern,
    val status: TriggerStatus,
    val correlationId: CorrelationId? = null
) : TriggerRecord()

data class TriggerSetActiveRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val correlationId: CorrelationId? = null
) : TriggerRecord()


data class TriggerSetInactiveRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val correlationId: CorrelationId? = null
) : TriggerRecord()