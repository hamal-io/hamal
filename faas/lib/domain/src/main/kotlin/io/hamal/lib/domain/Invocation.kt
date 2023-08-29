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
@SerialName("AdhocInvocation")
data class AdhocInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()

@Serializable
@SerialName("FixedRateInvocation")
data class FixedRateInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()

@Serializable
@SerialName("FuncInvocation")
data class FuncInvocation(
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()

@Serializable
@SerialName("EventInvocation")
data class EventInvocation(
    val events: List<Event>,
    override val invokedAt: InvokedAt = InvokedAt.now()
) : Invocation()