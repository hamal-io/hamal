package io.hamal.lib.domain.request

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
    val cron: CronPattern?
}

data class TriggerCreateRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: TriggerId,
    val type: TriggerType,
    val workspaceId: WorkspaceId,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    val namespaceId: NamespaceId,
    val correlationId: CorrelationId? = null,
    val duration: TriggerDuration? = null,
    val topicId: TopicId? = null,
    val cron: CronPattern? = null
) : Requested()

data class TriggerStatusRequested(
    override val requestId: RequestId,
    override val requestedBy: AuthId,
    override var requestStatus: RequestStatus,
    val id: TriggerId,
    val status: TriggerStatus
) : Requested()


interface TriggerInvokeRequest {
    val correlationId: CorrelationId?
    val inputs: InvocationInputs?
}

