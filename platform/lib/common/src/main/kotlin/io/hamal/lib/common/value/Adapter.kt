package io.hamal.lib.common.value

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal object ValueJsonAdapters {

    internal object Instant : JsonAdapter<ValueInstant> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueInstant {
            return ValueInstant(java.time.Instant.from(formatter.parse(json.asString)))
        }

        override fun serialize(src: ValueInstant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(formatter.format(src.instantValue))
        }

        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)
    }

    class InstantVariable<TYPE : ValueVariableInstant>(
        val ctor: (ValueInstant) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueInstant::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueInstant::class.java)
        }
    }

    data object Number : JsonAdapter<ValueNumber> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNumber {
            return ValueNumber(json.asDouble)
        }

        override fun serialize(src: ValueNumber, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.doubleValue)
        }
    }

    class NumberVariable<TYPE : ValueVariableNumber>(
        val ctor: (ValueNumber) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(ValueNumber(json.asDouble))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.value.doubleValue)
        }
    }

    class ObjectVariable<TYPE : ValueVariableObject>(
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

    class SnowflakeIdVariable<TYPE : ValueVariableSnowflakeId>(
        val ctor: (ValueSnowflakeId) -> TYPE
    ) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(ValueSnowflakeId(SnowflakeId(json.asString)))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.stringValue)
        }
    }

    class StringVariable<TYPE : ValueVariableString>(
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


