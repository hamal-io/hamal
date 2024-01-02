package io.hamal.lib.domain.vo

import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain.vo.base.MapValueObject
import io.hamal.lib.kua.type.MapType

class InvocationInputs(override val value: MapType = MapType()) : MapValueObject()

sealed interface Invocation

data class EventInvocation(val events: List<Event>) : Invocation

data class HookInvocation(
    val method: HookMethod,
    val headers: HookHeaders,
    val parameters: HookParameters,
    val content: HookContent
) : Invocation


data class EndpointInvocation(
    val method: EndpointMethod,
    val headers: EndpointHeaders,
    val parameters: EndpointParameters,
    val content: EndpointContent
) : Invocation

object EmptyInvocation : Invocation

