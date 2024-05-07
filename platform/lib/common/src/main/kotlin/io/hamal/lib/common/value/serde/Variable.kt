package io.hamal.lib.common.value.serde

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterGeneric
import io.hamal.lib.common.value.*
import java.lang.reflect.Type

object ValueVariableAdapters {

    class Boolean<TYPE : ValueVariableBoolean>(val ctor: (ValueBoolean) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueBoolean::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueBoolean::class.java)
        }
    }

    class Code<TYPE : ValueVariableCode>(val ctor: (ValueCode) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueCode::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueCode::class.java)
        }
    }

    class Enum<TYPE : ValueVariableEnum<*>>(val ctor: (ValueEnum) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueEnum::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueEnum::class.java)
        }
    }

    class Instant<TYPE : ValueVariableInstant>(val ctor: (ValueInstant) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueInstant::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueInstant::class.java)
        }
    }

    class Number<TYPE : ValueVariableNumber>(val ctor: (ValueNumber) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueNumber::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    class Object<TYPE : ValueVariableObject>(val ctor: (ValueObject) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueObject::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    class SnowflakeId<TYPE : ValueVariableSnowflakeId>(val ctor: (ValueSnowflakeId) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueSnowflakeId::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    class String<TYPE : ValueVariableString>(val ctor: (ValueString) -> TYPE) : AdapterGeneric<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueString::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueString::class.java)
        }
    }

}