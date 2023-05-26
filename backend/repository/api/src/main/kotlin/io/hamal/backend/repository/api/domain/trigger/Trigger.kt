package io.hamal.backend.repository.api.domain.trigger

import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Trigger : DomainObject<TriggerId> {
    abstract val name: TriggerName
    abstract val funcId: FuncId
}

@Serializable
class FixedDelayTrigger(
    override val id: TriggerId,
    override val name: TriggerName,
    override val funcId: FuncId
) : Trigger()

@Serializable
sealed class Invocation : DomainObject<InvocationId> {
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: Requester<TenantId>
}

@Serializable
data class AdhocInvocation(
    override val id: InvocationId = InvocationId(0), //FIXME
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<TenantId> = Requester.tenant(TenantId(0)), //FIXME
) : Invocation()

@Serializable
data class ManualInvocation(
    override val id: InvocationId = InvocationId(0), //FIXME
    val funcId: FuncId,
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<TenantId> = Requester.tenant(TenantId(0)), //FIXME
) : Invocation()

@Serializable
data class TriggerInvocation(
    override val id: InvocationId = InvocationId(0), //FIXME
    val trigger: Trigger,
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<TenantId> = Requester.tenant(TenantId(0)), //FIXME
) : Invocation()