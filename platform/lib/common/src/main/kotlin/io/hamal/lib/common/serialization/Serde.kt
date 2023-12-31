package io.hamal.lib.common.serialization

import com.google.gson.*
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import java.lang.reflect.Type
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

interface GsonSerde<T : Any> : JsonSerializer<T>, JsonDeserializer<T>

internal object HotArrayAdapter : GsonSerde<HotArray> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HotArray {
        return GsonTransform.toNode(json).asArray()
    }

    override fun serialize(src: HotArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return GsonTransform.fromNode(src)
    }
}

internal object HotObjectAdapter : GsonSerde<HotObject> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HotObject {
        return GsonTransform.toNode(json).asObject()
    }

    override fun serialize(src: HotObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return GsonTransform.fromNode(src)
    }
}

internal object InstantAdapter : GsonSerde<Instant> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Instant {
        return Instant.from(formatter.parse(json.asString))
    }

    override fun serialize(src: Instant?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }


    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)
}

