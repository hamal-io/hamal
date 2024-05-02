package io.hamal.lib.common.serialization

import com.google.gson.*
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
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


object JsonAdapters {

    class Instant<TYPE : ValueVariableInstant>(
        val ctor: (ValueInstant) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(ValueInstant(context.deserialize(json, java.time.Instant::class.java)))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value.instantValue, java.time.Instant::class.java)
        }
    }

    class Number<TYPE : ValueVariableNumber>(
        val ctor: (ValueNumber) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(ValueNumber(json.asDouble))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.value.doubleValue)
        }
    }

    class Object<TYPE : ValueVariableObject>(
        val ctor: (ValueObject) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            TODO()
//            return ctor(context.deserialize(json, HotObject::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            TODO()
//            return context.serialize(src.value, HotObject::class.java)
        }
    }


    class SnowflakeId<TYPE : ValueVariableSnowflakeId>(
        val ctor: (ValueSnowflakeId) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(ValueSnowflakeId(SnowflakeId(json.asString)))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.stringValue)
        }
    }


    class String<TYPE : ValueVariableString>(
        val ctor: (ValueString) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(ValueString(json.asString))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.value.stringValue)
        }
    }

}


