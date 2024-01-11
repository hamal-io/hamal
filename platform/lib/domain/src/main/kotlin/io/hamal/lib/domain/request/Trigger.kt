package io.hamal.lib.domain.request

import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlin.time.Duration


interface TriggerCreateRequest {
    val type: TriggerType
    val name: TriggerName
    val funcId: FuncId
    val inputs: TriggerInputs
    val correlationId: CorrelationId?
    val duration: Duration?
    val topicId: TopicId?
    val hookId: HookId?
    val hookMethod: HookMethod?
    val cron: CronPattern?
}

data class TriggerCreateRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val groupId: GroupId,
    val triggerType: TriggerType,
    val triggerId: TriggerId,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    var flowId: FlowId,
    val correlationId: CorrelationId? = null,
    val duration: Duration? = null,
    val topicId: TopicId? = null,
    val hookId: HookId? = null,
    val hookMethod: HookMethod? = null,
    val cron: CronPattern? = null
) : Requested()

data class TriggerStatusRequested(
    override val id: RequestId,
    override var status: RequestStatus,
    val triggerId: TriggerId,
    val triggerStatus: TriggerStatus
) : Requested()

