package io.hamal.lib.domain.submitted

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonSerde
import java.lang.reflect.Type

class SubmittedTypeAdapter : GsonSerde<Submitted> {
    override fun serialize(submitted: Submitted, type: Type, ctx: JsonSerializationContext): JsonElement {
        return ctx.serialize(submitted)
    }

    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): Submitted {
        return when (element.asJsonObject.get("submissionType").asString) {
            "AccountCreateSubmitted" -> ctx.deserialize(element, AccountCreateSubmitted::class.java)
            "AuthLoginEmailSubmitted" -> ctx.deserialize(element, AuthLoginEmailSubmitted::class.java)
            else -> TODO()
        }
    }
}