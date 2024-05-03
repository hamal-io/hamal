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

object ValueHonAdapters {

    internal data object Array : AdapterHon<ValueArray> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueArray {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueArray
        }

        override fun serialize(src: ValueArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    internal data object Boolean : AdapterHon<ValueBoolean> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueBoolean {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueBoolean
        }

        override fun serialize(src: ValueBoolean, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    class BooleanVariable<TYPE : ValueVariableBoolean>(val ctor: (ValueBoolean) -> TYPE) : AdapterHon<TYPE> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(hon, ValueBoolean::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueBoolean::class.java)
        }
    }

    internal data object Decimal : AdapterHon<ValueDecimal> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueDecimal {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueDecimal
        }

        override fun serialize(src: ValueDecimal, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    internal data object Instant : AdapterHon<ValueInstant> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueInstant {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueInstant
        }

        override fun serialize(src: ValueInstant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    class InstantVariable<TYPE : ValueVariableInstant>(val ctor: (ValueInstant) -> TYPE) : AdapterHon<TYPE> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(hon, ValueInstant::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueInstant::class.java)
        }
    }

    internal data object Nil : AdapterHon<ValueNil> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNil {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueNil
        }

        override fun serialize(src: ValueNil, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    internal data object Number : AdapterHon<ValueNumber> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueNumber {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueNumber
        }

        override fun serialize(src: ValueNumber, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    class NumberVariable<TYPE : ValueVariableNumber>(val ctor: (ValueNumber) -> TYPE) : AdapterHon<TYPE> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(hon, ValueNumber::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    internal data object Object : AdapterHon<ValueObject> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueObject {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueObject
        }

        override fun serialize(src: ValueObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    class ObjectVariable<TYPE : ValueVariableObject>(val ctor: (ValueObject) -> TYPE) : AdapterHon<TYPE> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(hon, ValueObject::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    internal data object SnowflakeId : AdapterHon<ValueSnowflakeId> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueSnowflakeId {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueSnowflakeId
        }

        override fun serialize(src: ValueSnowflakeId, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    class SnowflakeIdVariable<TYPE : ValueVariableSnowflakeId>(val ctor: (ValueSnowflakeId) -> TYPE) : AdapterHon<TYPE> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(hon, ValueSnowflakeId::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value)
        }
    }

    internal data object String : AdapterHon<ValueString> {
        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ValueString {
            return ValueHonTransform.fromHon(GsonTransform.toNode(hon)) as ValueString
        }

        override fun serialize(src: ValueString, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(ValueHonTransform.toHon(src))
        }
    }

    class StringVariable<TYPE : ValueVariableString>(val ctor: (ValueString) -> TYPE) : AdapterHon<TYPE> {

        override fun deserialize(hon: JsonElement, typeOfT: Type, context: JsonDeserializationContext): TYPE {
            return ctor(context.deserialize(hon, ValueString::class.java))
        }

        override fun serialize(src: TYPE, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src.value, ValueString::class.java)
        }
    }

}

internal object ValueHonTransform {

    fun fromHon(node: JsonNode<*>): Value {
        require(node is JsonObject)
        val type = TypeIdentifier(ValueString(node.stringValue("type")))
        return when (type) {
            TypeBoolean.identifier -> ValueBoolean(node.stringValue("value"))
            TypeDecimal.identifier -> ValueDecimal(node.stringValue("value"))
            TypeInstant.identifier -> ValueInstant(InstantUtils.parse(node.stringValue("value")))
            TypeNil.identifier -> ValueNil
            TypeNumber.identifier -> ValueNumber(node.stringValue("value"))
            TypeSnowflakeId.identifier -> ValueSnowflakeId(node.stringValue("value"))
            TypeString.identifier -> ValueString(node.stringValue("value"))
            else -> fromObject(node.asObject("value"))
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
        obj.properties.forEach { property ->
            builder[property.identifier.stringValue] = toHon(property.value)
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
