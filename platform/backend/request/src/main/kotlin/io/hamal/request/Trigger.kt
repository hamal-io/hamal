package io.hamal.request

import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import kotlin.time.Duration


interface CreateTriggerReq {
    val type: TriggerType
    val name: TriggerName
    val funcId: FuncId
    val namespaceId: NamespaceId?
    val inputs: TriggerInputs
    val correlationId: CorrelationId?
    val duration: Duration?
    val topicId: TopicId?
    val hookId: HookId?
    val hookMethods: Set<HookMethod>?
}
