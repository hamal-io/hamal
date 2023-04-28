package io.hamal.backend.core.model

import io.hamal.backend.core.DomainObject
import io.hamal.lib.vo.*

data class Trigger(
    val id: TriggerId
) : DomainObject


data class InvokedTrigger(
    val id: InvokedTriggerId,
    val triggerId: TriggerId,
    val reference: TriggerReference,
    val jobDefinitionId: JobDefinitionId,
    val accountId: AccountId,
    val invokedAt: InvokedAt
) : DomainObject