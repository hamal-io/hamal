package io.hamal.lib.nodes.control

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter

enum class ControlType {
    Condition,

    ConstantBoolean,
    ConstantDecimal,
    ConstantString,

    InputBoolean,
    InputDecimal,
    InputString,

    String
}

sealed interface Control {

    val type: ControlType

    object Adapter : JsonAdapter<Control> {

        override fun serialize(
            src: Control,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            TODO("Not yet implemented")
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): Control {
            val type = ControlType.valueOf(json.asJsonObject.get("type").asString)

            return when (type) {
                ControlType.Condition -> context.deserialize(json, ControlCondition::class.java)
                ControlType.ConstantBoolean -> context.deserialize(json, ControlConstantBoolean::class.java)
                ControlType.ConstantDecimal -> context.deserialize(json, ControlConstantDecimal::class.java)
                ControlType.ConstantString -> context.deserialize(json, ControlConstantString::class.java)
                ControlType.InputBoolean -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType.InputDecimal -> context.deserialize(json, ControlInputDecimal::class.java)
                ControlType.InputString -> context.deserialize(json, ControlInputString::class.java)
                ControlType.String -> context.deserialize(json, ControlString::class.java)
            }
        }
    }
}


sealed interface ControlExtension {

    val type: ControlType

    object Adapter : JsonAdapter<ControlExtension> {

        override fun serialize(
            src: ControlExtension,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            TODO("Not yet implemented")
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): ControlExtension {
            val type = ControlType.valueOf(json.asJsonObject.get("type").asString)

            return when (type) {
                ControlType.Condition -> context.deserialize(json, ControlCondition::class.java)
                ControlType.ConstantBoolean -> context.deserialize(json, ControlConstantBoolean::class.java)
                ControlType.ConstantDecimal -> context.deserialize(json, ControlConstantDecimal::class.java)
                ControlType.ConstantString -> context.deserialize(json, ControlConstantString::class.java)
                ControlType.InputBoolean -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType.InputDecimal -> context.deserialize(json, ControlInputDecimal::class.java)
                ControlType.InputString -> context.deserialize(json, ControlInputString::class.java)
                ControlType.String -> context.deserialize(json, ControlString::class.java)
            }
        }
    }
}