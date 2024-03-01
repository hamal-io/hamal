package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*


interface TriggerCreateRequest {
    val type: TriggerType
    val name: TriggerName
    val funcId: FuncId
    val inputs: TriggerInputs?
    val correlationId: CorrelationId?
    val duration: TriggerDuration?
    val topicId: TopicId?
    val hookId: HookId?
    val hookMethod: HookMethod?
    val cron: CronPattern?
}

data class TriggerCreateRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val workspaceId: WorkspaceId,
    val triggerType: TriggerType,
    val triggerId: TriggerId,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    var namespaceId: NamespaceId,
    val correlationId: CorrelationId? = null,
    val duration: TriggerDuration? = null,
    val topicId: TopicId? = null,
    val hookId: HookId? = null,
    val hookMethod: HookMethod? = null,
    val cron: CronPattern? = null
) : Requested()

data class TriggerStatusRequested(
    override val id: RequestId,
    override val by: AuthId,
    override var status: RequestStatus,
    val triggerId: TriggerId,
    val triggerStatus: TriggerStatus
) : Requested()

