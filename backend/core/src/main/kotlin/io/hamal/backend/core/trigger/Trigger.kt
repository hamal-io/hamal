package io.hamal.backend.core.trigger

import io.hamal.backend.core.trigger.Trigger.ManualTrigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Trigger : DomainObject<TriggerId> {

    abstract val reference: TriggerName

    @Serializable
    data class ManualTrigger(
        override val id: TriggerId,
        override val reference: TriggerName,
    ) : Trigger()

}

@Serializable
sealed class InvokedTrigger : DomainObject<InvokedTriggerId> {
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: Requester<TenantId>

    @Serializable
    data class Manual(
        override val id: InvokedTriggerId,
        val trigger: ManualTrigger,
        override val invokedAt: InvokedAt,
        override val invokedBy: Requester<TenantId>
    ) : InvokedTrigger()

}

@Serializable
data class AdhocTrigger(
    override val id: InvokedTriggerId,
    override val invokedAt: InvokedAt,
    override val invokedBy: Requester<TenantId>,
    val code: Code
) : InvokedTrigger()