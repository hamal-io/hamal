package io.hamal.lib.common.serialization

import com.google.gson.*
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import java.lang.reflect.Type
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

interface JsonAdapter<T : Any> : JsonSerializer<T>, JsonDeserializer<T>

internal object HotArrayAdapter : JsonAdapter<HotArray> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HotArray {
        return GsonTransform.toNode(json).asArray()
    }

    override fun serialize(src: HotArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return GsonTransform.fromNode(src)
    }
}

internal object HotObjectAdapter : JsonAdapter<HotObject> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HotObject {
        return GsonTransform.toNode(json).asObject()
    }

    override fun serialize(src: HotObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return GsonTransform.fromNode(src)
    }
}

internal object InstantAdapter : JsonAdapter<Instant> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Instant {
        return Instant.from(formatter.parse(json.asString))
    }

    override fun serialize(src: Instant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(formatter.format(src))
    }


    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)
}


class ValueObjectIdAdapter<TYPE : ValueObjectId>(
    val ctor: (SnowflakeId) -> TYPE
) : JsonAdapter<TYPE> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
        return ctor(SnowflakeId(json.asString))
    }

    override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.value.value.toString(16))
    }
}


class ValueObjectStringAdapter<TYPE : ValueObjectString>(
    val ctor: (String) -> TYPE
) : JsonAdapter<TYPE> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
        return ctor(json.asString)
    }

    override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.value)
    }
}


class ValueObjectIntAdapter<TYPE : ValueObjectInt>(
    val ctor: (Int) -> TYPE
) : JsonAdapter<TYPE> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
        return ctor(json.asInt)
    }

    override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.value)
    }
}


class ValueObjectInstantAdapter<TYPE : ValueObjectInstant>(
    val ctor: (Instant) -> TYPE
) : JsonAdapter<TYPE> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
        return ctor(context.deserialize(json, Instant::class.java))
    }

    override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src.value, Instant::class.java)
    }
}

class ValueObjectMapAdapter<TYPE : ValueObjectMap>(
    val ctor: (HotObject) -> TYPE
) : JsonAdapter<TYPE> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
        return ctor(context.deserialize(json, HotObject::class.java))
    }

    override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src.value, HotObject::class.java)
    }
}

