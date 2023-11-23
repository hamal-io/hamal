package io.hamal.lib.domain.vo

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain.vo.base.InputsSerializer
import io.hamal.lib.domain.vo.base.Map
import io.hamal.lib.kua.type.MapType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = InvocationInputs.Serializer::class)
class InvocationInputs(override val value: MapType = MapType()) : Map() {
    internal object Serializer : InputsSerializer<InvocationInputs>(::InvocationInputs)
}

@Serializable
sealed interface Invocation

@Serializable
@SerialName("EventInvocation")
data class EventInvocation(val events: List<Event>) : Invocation

@Serializable
@SerialName("HookInvocation")
data class HookInvocation(
    val method: HookMethod,
    val headers: HookHeaders,
    val parameters: HookParameters,
    val content: HookContent
) : Invocation


@Serializable
@SerialName("EndpointInvocation")
data class EndpointInvocation(
    val method: EndpointMethod,
    val headers: EndpointHeaders,
    val parameters: EndpointParameters,
    val content: EndpointContent
) : Invocation

@Serializable
@SerialName("EmptyInvocation")
object EmptyInvocation : Invocation

