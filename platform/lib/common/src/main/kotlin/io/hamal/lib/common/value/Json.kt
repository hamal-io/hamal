package io.hamal.lib.common.value

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.serialization.json.*
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object SerdeModuleJsonValue : SerdeModuleJson() {

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

        this[ValueArray::class] = ValueJsonAdapters.Array
        this[ValueBoolean::class] = ValueJsonAdapters.Boolean
        this[ValueDecimal::class] = ValueJsonAdapters.Decimal
        this[ValueInstant::class] = ValueJsonAdapters.Instant
        this[ValueNil::class] = ValueJsonAdapters.Nil
        this[ValueNumber::class] = ValueJsonAdapters.Number
        this[ValueObject::class] = ValueJsonAdapters.Object
        this[ValueSnowflakeId::class] = ValueJsonAdapters.SnowflakeId
        this[ValueString::class] = ValueJsonAdapters.String

    }
}

object ValueJsonAdapters {

    internal data object Array : JsonAdapter<ValueArray> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueArray {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json)) as ValueArray
        }

        override fun serialize(src: ValueArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    internal data object Boolean : JsonAdapter<ValueBoolean> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueBoolean {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json)) as ValueBoolean
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
            return ValueDecimal(json.asString)
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

    internal data object Nil : JsonAdapter<ValueNil> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNil {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json)) as ValueNil
        }

        override fun serialize(src: ValueNil, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    internal data object Number : JsonAdapter<ValueNumber> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNumber {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json)) as ValueNumber
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
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json)) as ValueObject
        }

        override fun serialize(src: ValueObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    class ObjectVariable<TYPE : ValueVariableObject>(val ctor: (ValueObject) -> TYPE) : JsonAdapter<TYPE> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(json, ValueObject::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    internal data object SnowflakeId : JsonAdapter<ValueSnowflakeId> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueSnowflakeId {
            return ValueSnowflakeId(SnowflakeId(json.asString))
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
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json)) as ValueString
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

    fun fromJson(node: JsonNode<*>): Value {
        return when (node) {
            is JsonArray -> fromArray(node.asArray())
            is JsonBoolean -> ValueBoolean(node.booleanValue)
            is JsonNull -> ValueNil
            is JsonNumber -> ValueNumber(node.doubleValue)
            is JsonObject -> fromObject(node)
            is JsonString -> ValueString(node.stringValue)
        }
    }

    private fun fromObject(obj: JsonObject): ValueObject {
        val builder = ValueObject.builder()
        obj.nodes.forEach { node ->
            builder[node.key] = fromJson(node.value)
        }
        return builder.build()
    }

    private fun fromArray(array: JsonArray): ValueArray {
        val builder = ValueArray.builder()
        array.nodes.forEach { node ->
            builder.append(fromJson(node))
        }
        return builder.build()
    }

    fun toJson(value: Value): JsonNode<*> {
        return when (value) {
            is ValueArray -> toArray(value)
            is ValueBoolean -> JsonBoolean(value.booleanValue)
            is ValueDecimal -> JsonString(value.toString())
            is ValueNil -> JsonNull
            is ValueNumber -> JsonNumber(value.doubleValue)
            is ValueObject -> toObject(value)
            is ValueSnowflakeId -> JsonString(value.stringValue)
            is ValueString -> JsonString(value.stringValue)
            else -> TODO("$value of class ${value::class} not supported yet")
        }
    }

    private fun toObject(obj: ValueObject): JsonObject {
        val builder = JsonObject.builder()
        obj.properties.forEach { property ->
            builder[property.identifier.stringValue] = toJson(property.value)
        }
        return builder.build()
    }

    private fun toArray(array: ValueArray): JsonArray {
        val builder = JsonArray.builder()
        array.value.forEach { value ->
            builder.append(toJson(value))
        }
        return builder.build()
    }
}
