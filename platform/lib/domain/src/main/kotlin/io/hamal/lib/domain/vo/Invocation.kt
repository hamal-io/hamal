package io.hamal.lib.domain.vo

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectHotObject
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.HookMethod

class InvocationInputs(override val value: HotObject = HotObject.empty) : ValueObjectHotObject()

class InvocationClass(override val value: String) : ValueObjectString()

sealed class Invocation {
    val `class`: InvocationClass = InvocationClass(this::class.simpleName!!)

    object Adapter : JsonAdapter<Invocation> {
        override fun serialize(
            src: Invocation,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): Invocation {
            val invocationClass = json.asJsonObject.get("class").asString
            return context.deserialize(
                json, (classMapping[invocationClass]
                    ?: throw NotImplementedError("$invocationClass not supported")).java
            )
        }

        private val classMapping = listOf(
            Adhoc::class,
            Endpoint::class,
            Event::class,
            Func::class,
            Hook::class,
            Schedule::class
        ).associateBy { it.simpleName }

    }

    data object Adhoc : Invocation()

    data object Func : Invocation()

    data class Endpoint(
        val method: EndpointMethod,
        val headers: EndpointHeaders,
        val parameters: EndpointParameters,
        val content: EndpointContent
    ) : Invocation()

    data class Event(
        val events: List<io.hamal.lib.domain.vo.Event>
    ) : Invocation()

    data class Hook(
        val method: HookMethod,
        val headers: HookHeaders,
        val parameters: HookParameters,
        val content: HookContent
    ) : Invocation()


    data object Schedule : Invocation()
}

