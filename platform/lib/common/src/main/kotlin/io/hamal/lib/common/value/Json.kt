package io.hamal.lib.common.value

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.serialization.SerializationModule
import io.hamal.lib.common.serialization.json.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object ValueJsonModule : SerializationModule() {

    init {
        this[FieldIdentifier::class] = ValueJsonAdapters.StringVariable(::FieldIdentifier)

        this[io.hamal.lib.common.value.Type::class] = object : JsonAdapter<io.hamal.lib.common.value.Type> {
            override fun serialize(
                src: io.hamal.lib.common.value.Type,
                typeOfSrc: Type,
                context: JsonSerializationContext
            ): JsonElement {
                return JsonPrimitive(src.identifier.stringValue)
            }

            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): io.hamal.lib.common.value.Type {
                return when (TypeIdentifier(json.asString)) {
                    TypeBoolean.identifier -> TypeBoolean
                    TypeNumber.identifier -> TypeNumber
                    TypeString.identifier -> TypeString
                    else -> TODO("Not implemented ${json.asString}")
                }
            }
        }

        this[ValueBoolean::class] = ValueJsonAdapters.Boolean
        this[ValueDecimal::class] = ValueJsonAdapters.Decimal
        this[ValueInstant::class] = ValueJsonAdapters.Instant
        this[ValueNumber::class] = ValueJsonAdapters.Number
        this[ValueObject::class] = ValueJsonAdapters.Object
        this[ValueSnowflakeId::class] = ValueJsonAdapters.SnowflakeId
        this[ValueString::class] = ValueJsonAdapters.String

    }
}

object ValueJsonAdapters {

    internal data object Boolean : JsonAdapter<ValueBoolean> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueBoolean {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueBoolean::class.java) as ValueBoolean
        }

        override fun serialize(src: ValueBoolean, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    class BooleanVariable<TYPE : ValueVariableBoolean>(val ctor: (ValueBoolean) -> TYPE) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueBoolean::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueBoolean::class.java)
        }
    }

    internal data object Decimal : JsonAdapter<ValueDecimal> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueDecimal {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueDecimal::class.java) as ValueDecimal
        }

        override fun serialize(src: ValueDecimal, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    internal data object Instant : JsonAdapter<ValueInstant> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueInstant {
            return ValueInstant(java.time.Instant.from(formatter.parse(json.asString)))
        }

        override fun serialize(src: ValueInstant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(formatter.format(src.instantValue))
        }

        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)
    }

    class InstantVariable<TYPE : ValueVariableInstant>(val ctor: (ValueInstant) -> TYPE) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueInstant::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueInstant::class.java)
        }
    }

    internal data object Number : JsonAdapter<ValueNumber> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNumber {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueNumber::class.java) as ValueNumber
        }

        override fun serialize(src: ValueNumber, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    class NumberVariable<TYPE : ValueVariableNumber>(val ctor: (ValueNumber) -> TYPE) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(ValueNumber(json.asDouble))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.value.doubleValue)
        }
    }

    internal data object Object : JsonAdapter<ValueObject> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueObject {
            val obj =
                context.deserialize<io.hamal.lib.common.serialization.json.JsonObject>(json, io.hamal.lib.common.serialization.json.JsonObject::class.java)

            TODO()
//            val obj = json.asJsonObject
//            return ValueObject.builder()
//                .also { builder ->
//                    obj.entrySet().forEach { entry ->
//                        builder[entry.key] = context.deserialize<Value>(entry.value, Value::class.java)
//                    }
//                }
//                .build()
        }

        override fun serialize(src: ValueObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
//            return JsonObject().also { obj ->
//                src.properties.forEach { property ->
//                    obj.add(property.identifier.stringValue, context.serialize(property.value))
//                }
//            }
            TODO()
        }
    }

    class ObjectVariable<TYPE : ValueVariableObject>(val ctor: (ValueObject) -> TYPE) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            TODO()
//            return ctor(context.deserialize(json, HotObject::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            TODO()
//            return context.serialize(src.value, HotObject::class.java)
        }
    }

    internal data object SnowflakeId : JsonAdapter<ValueSnowflakeId> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueSnowflakeId {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueSnowflakeId::class.java) as ValueSnowflakeId
        }

        override fun serialize(src: ValueSnowflakeId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    class SnowflakeIdVariable<TYPE : ValueVariableSnowflakeId>(val ctor: (ValueSnowflakeId) -> TYPE) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueSnowflakeId::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    internal data object String : JsonAdapter<ValueString> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueString {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueString::class.java) as ValueString
        }

        override fun serialize(src: ValueString, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    class StringVariable<TYPE : ValueVariableString>(val ctor: (ValueString) -> TYPE) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueString::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueString::class.java)
        }
    }

}

internal object ValueJsonTransform {

    fun fromJson(node: JsonNode<*>, type: Type): Value {
        return when (node) {
            is JsonArray -> fromArray(node.asArray())
            is JsonBoolean -> ValueBoolean(node.booleanValue)
            is JsonNull -> ValueNil
            is JsonNumber -> ValueNumber(node.doubleValue)
            is JsonObject -> fromObject(node)
            is JsonString -> {
                return when (type) {
                    ValueString::class.java -> ValueString(node.stringValue)
                    ValueSnowflakeId::class.java -> ValueSnowflakeId(SnowflakeId(node.stringValue))
                    ValueDecimal::class.java -> ValueDecimal(node.stringValue)
                    else -> TODO()
                }
            }
        }
    }

    fun fromObject(obj: JsonObject): ValueObject {
        TODO()
    }

    fun fromArray(array: JsonArray): ValueArray {
        TODO()
    }

    fun toJson(value: Value): JsonNode<*> {
        return when (value) {
            is ValueBoolean -> JsonBoolean(value.booleanValue)
            is ValueDecimal -> JsonString(value.toString())
            is ValueNumber -> JsonNumber(value.doubleValue)
            is ValueSnowflakeId -> JsonString(value.stringValue)
            is ValueString -> JsonString(value.stringValue)
            else -> TODO("${value} of class ${value::class} not supported yet")
        }
    }
}
