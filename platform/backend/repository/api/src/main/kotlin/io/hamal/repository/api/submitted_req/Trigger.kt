package io.hamal.repository.api.submitted_req

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class SubmittedCreateTriggerReq(
    override val reqId: ReqId,
    override var status: ReqStatus,
    override val groupId: GroupId,
    val type: TriggerType,
    val id: TriggerId,
    val name: TriggerName,
    val funcId: FuncId,
    val inputs: TriggerInputs,
    var namespaceId: NamespaceId? = null,
    val correlationId: CorrelationId? = null,
    val duration: Duration? = null,
    val topicId: TopicId? = null,
    val hookId: HookId? = null,
    val hookMethods: Set<HookMethod>? = null
) : SubmittedReqWithGroupId


