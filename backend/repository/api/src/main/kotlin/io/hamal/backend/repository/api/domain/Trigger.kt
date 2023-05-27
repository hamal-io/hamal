package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain._enum.TriggerType.Event
import io.hamal.lib.domain._enum.TriggerType.FixedRate
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
sealed class Trigger : DomainObject<TriggerId>() {
    abstract val name: TriggerName
    abstract val funcId: FuncId
    abstract val type: TriggerType


}

@Serializable
class FixedRateTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    val duration: Duration
) : Trigger() {
    override val type = FixedRate
}

@Serializable
class EventTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId,
    val topicId: TopicId
) : Trigger() {
    override val type = Event
}

@Serializable
sealed class Invocation {
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: Requester<TenantId>
}

@Serializable
data class AdhocInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<TenantId> = Requester.tenant(TenantId(0)), //FIXME
) : Invocation()

@Serializable
data class ApiInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<TenantId> = Requester.tenant(TenantId(0)), //FIXME
) : Invocation()

@Serializable
data class TriggerInvocation(
    val trigger: Trigger,
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<TenantId> = Requester.tenant(TenantId(0)), //FIXME
) : Invocation()

@Serializable
data class EventInvocation(
//    val events: List<TenantE>
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<TenantId> = Requester.tenant(TenantId(0)), //FIXME
) : Invocation()