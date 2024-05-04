package io.hamal.lib.common.value.serde

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.serialization.json.*
import io.hamal.lib.common.util.InstantUtils
import io.hamal.lib.common.value.*
import java.lang.reflect.Type

object SerdeModuleValueJson : SerdeModuleJson() {

    init {
        this[ValueType::class] = ValueTypeAdapter

        this[ValueArray::class] = ValueJsonAdapters.Array
        this[ValueBoolean::class] = ValueJsonAdapters.Boolean
        this[ValueCode::class] = ValueJsonAdapters.Code
        this[ValueDecimal::class] = ValueJsonAdapters.Decimal
        this[ValueEnum::class] = ValueJsonAdapters.Enum
        this[ValueInstant::class] = ValueJsonAdapters.Instant
        this[ValueNil::class] = ValueJsonAdapters.Nil
        this[ValueNumber::class] = ValueJsonAdapters.Number
        this[ValueObject::class] = ValueJsonAdapters.Object
        this[ValueSnowflakeId::class] = ValueJsonAdapters.SnowflakeId
        this[ValueString::class] = ValueJsonAdapters.String

    }
}

internal object ValueJsonAdapters {

    data object Array : AdapterJson<ValueArray> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueArray {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueArray::class.java) as ValueArray
        }

        override fun serialize(src: ValueArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Boolean : AdapterJson<ValueBoolean> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueBoolean {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueBoolean::class.java) as ValueBoolean
        }

        override fun serialize(src: ValueBoolean, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Code : AdapterJson<ValueCode> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueCode {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueCode::class.java) as ValueCode
        }

        override fun serialize(src: ValueCode, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Decimal : AdapterJson<ValueDecimal> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueDecimal {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueDecimal::class.java) as ValueDecimal
        }

        override fun serialize(src: ValueDecimal, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Enum : AdapterJson<ValueEnum> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueEnum {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueEnum::class.java) as ValueEnum
        }

        override fun serialize(src: ValueEnum, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Instant : AdapterJson<ValueInstant> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueInstant {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueInstant::class.java) as ValueInstant
        }

        override fun serialize(src: ValueInstant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Nil : AdapterJson<ValueNil> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNil {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueNil::class.java) as ValueNil
        }

        override fun serialize(src: ValueNil, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Number : AdapterJson<ValueNumber> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNumber {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueNumber::class.java) as ValueNumber
        }

        override fun serialize(src: ValueNumber, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object Object : AdapterJson<ValueObject> {

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueObject {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueObject::class.java) as ValueObject
        }

        override fun serialize(src: ValueObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object SnowflakeId : AdapterJson<ValueSnowflakeId> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueSnowflakeId {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueSnowflakeId::class.java) as ValueSnowflakeId
        }

        override fun serialize(src: ValueSnowflakeId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }

    data object String : AdapterJson<ValueString> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueString {
            return ValueJsonTransform.fromJson(GsonTransform.toNode(json), ValueString::class.java) as ValueString
        }

        override fun serialize(src: ValueString, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueJsonTransform.toJson(src))
        }
    }
}

internal object ValueJsonTransform {

    fun fromJson(node: JsonNode<*>, type: Type?): Value {
        return when (node) {
            is JsonArray -> fromArray(node.asArray())
            is JsonBoolean -> ValueBoolean(node.booleanValue)
            is JsonNull -> ValueNil
            is JsonNumber -> ValueNumber(node.doubleValue)
            is JsonObject -> fromObject(node)
            is JsonString -> {
                when (type) {
                    null -> ValueString(node.stringValue)
                    ValueString::class.java -> ValueString(node.stringValue)
                    ValueSnowflakeId::class.java -> ValueSnowflakeId(node.stringValue)
                    ValueDecimal::class.java -> ValueDecimal(node.stringValue)
                    ValueInstant::class.java -> ValueInstant(InstantUtils.parse(node.stringValue))
                    ValueEnum::class.java -> ValueEnum(node.stringValue)
                    ValueCode::class.java -> ValueCode(node.stringValue)
                    else -> TODO()
                }
            }
        }
    }

    private fun fromObject(obj: JsonObject): ValueObject {
        val builder = ValueObject.builder()
        obj.nodes.forEach { node -> builder[node.key] = fromJson(node.value, null) }
        return builder.build()
    }

    private fun fromArray(array: JsonArray): ValueArray {
        val builder = ValueArray.builder()
        array.nodes.forEach { node ->
            builder.append(fromJson(node, null))
        }
        return builder.build()
    }

    fun toJson(value: Value): JsonNode<*> {
        return when (value) {
            is ValueArray -> toArray(value)
            is ValueBoolean -> JsonBoolean(value.booleanValue)
            is ValueCode -> JsonString(value.stringValue)
            is ValueDecimal -> JsonString(value.toString())
            is ValueEnum -> JsonString(value.stringValue)
            is ValueInstant -> JsonString(value.stringValue)
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
        obj.values.forEach { (key, value) ->
            builder[key] = toJson(value)
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
