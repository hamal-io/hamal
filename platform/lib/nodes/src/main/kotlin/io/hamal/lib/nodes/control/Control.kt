package io.hamal.lib.nodes.control

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.JsonAdapter

sealed interface Control {
    val type: Type

    enum class Type {
        Condition,

        ConstantBoolean,
        ConstantDecimal,
        ConstantString,

        InputBoolean,
        InputDecimal,
        InputString,

        String
    }

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
            val type = Type.valueOf(json.asJsonObject.get("type").asString)

            return when (type) {
                Type.Condition -> context.deserialize(json, ControlCondition::class.java)
                Type.ConstantBoolean -> context.deserialize(json, ControlConstantBoolean::class.java)
                Type.ConstantDecimal -> context.deserialize(json, ControlConstantDecimal::class.java)
                Type.ConstantString -> context.deserialize(json, ControlConstantString::class.java)
                Type.InputBoolean -> context.deserialize(json, ControlInputBoolean::class.java)
                Type.InputDecimal -> context.deserialize(json, ControlInputDecimal::class.java)
                Type.InputString -> context.deserialize(json, ControlInputString::class.java)
                Type.String -> context.deserialize(json, ControlString::class.java)
            }
        }
    }
}