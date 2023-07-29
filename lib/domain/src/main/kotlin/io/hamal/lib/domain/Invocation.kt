package io.hamal.lib.domain

import io.hamal.lib.domain.vo.InvokedAt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Invocation {
    // invoked at
    // invoked by
    abstract val invokedAt: InvokedAt
}

@Serializable
@SerialName("Adhoc")
data class AdhocInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()

@Serializable
@SerialName("FixedRate")
data class FixedRateInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()

@Serializable
@SerialName("OneShot")
data class OneShotInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()

@Serializable
@SerialName("Event")
data class EventInvocation(
    val events: List<Event>,
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()