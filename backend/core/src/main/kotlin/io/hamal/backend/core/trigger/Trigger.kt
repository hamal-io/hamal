package io.hamal.backend.core.trigger

import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Trigger : DomainObject<TriggerId> {

    abstract val name: TriggerName


}

@Serializable
sealed class InvokedTrigger : DomainObject<InvokedTriggerId> {
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: Requester<TenantId>
}

@Serializable
data class AdhocTrigger(
    override val id: InvokedTriggerId,
    override val invokedAt: InvokedAt,
    override val invokedBy: Requester<TenantId>,
    val code: Code
) : InvokedTrigger()