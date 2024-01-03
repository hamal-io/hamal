package io.hamal.lib.domain.request

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonSerde
import java.lang.reflect.Type

class TypeAdapter : GsonSerde<Requested> {
    override fun serialize(submitted: Requested, type: Type, ctx: JsonSerializationContext): JsonElement {
        return ctx.serialize(submitted)
    }

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): Requested {
        return when (element.asJsonObject.get("type").asString) {
            "AccountCreateRequested" -> ctx.deserialize(element, AccountCreateRequested::class.java)
            "AuthLoginEmailRequested" -> ctx.deserialize(element, AuthLoginEmailRequested::class.java)
            else -> TODO()
        }
    }
}