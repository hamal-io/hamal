package io.hamal.lib.common.value.serde

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterHon
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.SerdeModuleHon
import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonNode
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.common.util.InstantUtils
import io.hamal.lib.common.value.*
import java.lang.reflect.Type

object SerdeModuleValueHon : SerdeModuleHon() {

    init {
        this[ValueArray::class] = ValueHonAdapters.Array
        this[ValueBoolean::class] = ValueHonAdapters.Boolean
        this[ValueDecimal::class] = ValueHonAdapters.Decimal
        this[ValueInstant::class] = ValueHonAdapters.Instant
        this[ValueNil::class] = ValueHonAdapters.Nil
        this[ValueNumber::class] = ValueHonAdapters.Number
        this[ValueObject::class] = ValueHonAdapters.Object
        this[ValueSnowflakeId::class] = ValueHonAdapters.SnowflakeId
        this[ValueString::class] = ValueHonAdapters.String
    }
}

internal object ValueHonAdapters {

    data object Array : AdapterHon<ValueArray> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueArray {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueArray
        }

        override fun serialize(src: ValueArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object Boolean : AdapterHon<ValueBoolean> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueBoolean {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueBoolean
        }

        override fun serialize(src: ValueBoolean, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object Decimal : AdapterHon<ValueDecimal> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueDecimal {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueDecimal
        }

        override fun serialize(src: ValueDecimal, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object Instant : AdapterHon<ValueInstant> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueInstant {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueInstant
        }

        override fun serialize(src: ValueInstant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object Nil : AdapterHon<ValueNil> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNil {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueNil
        }

        override fun serialize(src: ValueNil, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object Number : AdapterHon<ValueNumber> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNumber {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueNumber
        }

        override fun serialize(src: ValueNumber, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object Object : AdapterHon<ValueObject> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueObject {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueObject
        }

        override fun serialize(src: ValueObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object SnowflakeId : AdapterHon<ValueSnowflakeId> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueSnowflakeId {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueSnowflakeId
        }

        override fun serialize(src: ValueSnowflakeId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    data object String : AdapterHon<ValueString> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueString {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueString
        }

        override fun serialize(src: ValueString, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }
}

internal object ValueHonTransform {

    fun fromHon(node: JsonNode<*>): Value {
        require(node is JsonObject)
        val type = TypeIdentifier(ValueString(node.stringValue("type")))
        return when (type) {
            TypeArray.identifier -> fromArray(node.asArray("value"))
            TypeBoolean.identifier -> ValueBoolean(node.stringValue("value"))
            TypeDecimal.identifier -> ValueDecimal(node.stringValue("value"))
            TypeInstant.identifier -> ValueInstant(InstantUtils.parse(node.stringValue("value")))
            TypeNil.identifier -> ValueNil
            TypeNumber.identifier -> ValueNumber(node.stringValue("value"))
            TypeObject.identifier -> fromObject(node.asObject("value"))
            TypeSnowflakeId.identifier -> ValueSnowflakeId(node.stringValue("value"))
            TypeString.identifier -> ValueString(node.stringValue("value"))
            else -> TODO()
        }
    }

    private fun fromObject(obj: JsonObject): ValueObject {
        val builder = ValueObject.builder()
        obj.nodes.forEach { node ->
            builder[node.key] = fromHon(node.value)
        }
        return builder.build()
    }

    private fun fromArray(array: JsonArray): ValueArray {
        val builder = ValueArray.builder()
        array.nodes.forEach { node ->
            builder.append(fromHon(node))
        }
        return builder.build()
    }

    fun toHon(value: Value): JsonNode<*> {
        val builder = JsonObject.builder().set("type", value.type.identifier.stringValue)

        return when (value) {
            is ValueArray -> builder.set("value", toArray(value))
            is ValueBoolean -> builder.set("value", value.stringValue)
            is ValueDecimal -> builder.set("value", value.stringValue)
            is ValueInstant -> builder.set("value", value.stringValue)
            is ValueNil -> builder
            is ValueNumber -> builder.set("value", value.stringValue)
            is ValueObject -> builder.set("value", toObject(value))
            is ValueSnowflakeId -> builder.set("value", value.stringValue)
            is ValueString -> builder.set("value", value.stringValue)
            else -> TODO("$value of class ${value::class} not supported yet")
        }.build()
    }

    private fun toObject(obj: ValueObject): JsonObject {
        val builder = JsonObject.builder()
        obj.values.forEach { (key, value) ->
            builder[key.stringValue] = toHon(value)
        }
        return builder.build()
    }

    private fun toArray(array: ValueArray): JsonArray {
        val builder = JsonArray.builder()
        array.value.forEach { value ->
            builder.append(toHon(value))
        }
        return builder.build()
    }
}
