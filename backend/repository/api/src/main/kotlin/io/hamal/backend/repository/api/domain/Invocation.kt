package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.vo.InvokedAt
import io.hamal.lib.domain.vo.AccountId
import kotlinx.serialization.Serializable

@Serializable
sealed class Invocation {
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: Requester<AccountId>
}

@Serializable
data class AdhocInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<AccountId> = Requester.tenant(AccountId(0)), //FIXME
) : Invocation()

@Serializable
data class OneshotInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<AccountId> = Requester.tenant(AccountId(0)), //FIXME
) : Invocation()

@Serializable
data class FixedRateInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<AccountId> = Requester.tenant(AccountId(0)), //FIXME
) : Invocation()

@Serializable
data class EventInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now(),//FIXME
    override val invokedBy: Requester<AccountId> = Requester.tenant(AccountId(0)), //FIXME
) : Invocation()