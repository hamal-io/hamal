package io.hamal.repository.record.trigger

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.vo.*
import io.hamal.repository.record.Record
import io.hamal.repository.record.RecordAdapter
import io.hamal.repository.record.RecordSequence
import io.hamal.repository.record.RecordedAt

sealed class TriggerRecord(
    @Transient
    override var recordSequence: RecordSequence? = null,
    @Transient
    override var recordedAt: RecordedAt? = null
) : Record<TriggerId>() {
    internal object Adapter : RecordAdapter<TriggerRecord>(
        listOf(
            FixedRateTriggerCreatedRecord::class,
            EventTriggerCreatedRecord::class,
            HookTriggerCreatedRecord::class,
            CronTriggerCreatedRecord::class,
            TriggerSetActiveRecord::class,
            TriggerSetInactiveRecord::class
        )
    )
}

data class FixedRateTriggerCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val namespaceId: NamespaceId,
    val name: TriggerName,
    val inputs: TriggerInputs,
    val duration: TriggerDuration,
    val status: TriggerStatus,
    val correlationId: CorrelationId? = null
) : TriggerRecord()

data class EventTriggerCreatedRecord(
    override val cmdId: CmdId,
    override val entityId: TriggerId,
    val groupId: GroupId,
    val funcId: FuncId,
    val namespaceId: NamespaceId,
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
    val namespaceId: NamespaceId,
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
    val namespaceId: NamespaceId,
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