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
            FixedRateCreated::class,
            EventCreated::class,
            HookCreated::class,
            CronCreated::class,
            SetActive::class,
            SetInactive::class
        )
    )

    data class FixedRateCreated(
        override val cmdId: CmdId,
        override val entityId: TriggerId,
        val workspaceId: WorkspaceId,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val name: TriggerName,
        val inputs: TriggerInputs,
        val duration: TriggerDuration,
        val status: TriggerStatus,
        val correlationId: CorrelationId? = null
    ) : TriggerRecord()

    data class EventCreated(
        override val cmdId: CmdId,
        override val entityId: TriggerId,
        val workspaceId: WorkspaceId,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val name: TriggerName,
        val inputs: TriggerInputs,
        val topicId: TopicId,
        val status: TriggerStatus,
        val correlationId: CorrelationId? = null
    ) : TriggerRecord()

    data class HookCreated(
        override val cmdId: CmdId,
        override val entityId: TriggerId,
        val workspaceId: WorkspaceId,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val name: TriggerName,
        val inputs: TriggerInputs,
        val hookId: HookId,
        val hookMethod: HookMethod,
        val status: TriggerStatus,
        val correlationId: CorrelationId? = null
    ) : TriggerRecord()

    data class CronCreated(
        override val cmdId: CmdId,
        override val entityId: TriggerId,
        val workspaceId: WorkspaceId,
        val funcId: FuncId,
        val namespaceId: NamespaceId,
        val name: TriggerName,
        val inputs: TriggerInputs,
        val cron: CronPattern,
        val status: TriggerStatus,
        val correlationId: CorrelationId? = null
    ) : TriggerRecord()

    data class SetActive(
        override val cmdId: CmdId,
        override val entityId: TriggerId
    ) : TriggerRecord()


    data class SetInactive(
        override val cmdId: CmdId,
        override val entityId: TriggerId
    ) : TriggerRecord()
}

