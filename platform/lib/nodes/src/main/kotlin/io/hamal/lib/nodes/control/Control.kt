package io.hamal.lib.nodes.control

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.nodes.control.Control.Type

class ControlId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

data object ControlNone : Control {
    override val id: ControlId get() = ControlId(0)
    override val type: Type get() = Type.None
}

sealed interface Control {
    val id: ControlId
    val type: Type

    enum class Type {
        Condition,

        ConstantBoolean,
        ConstantDecimal,
        ConstantString,

        InputBoolean,
        InputDecimal,
        InputString,

        None,

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
                Type.None -> context.deserialize(json, ControlNone::class.java)
                Type.String -> context.deserialize(json, ControlString::class.java)
            }
        }
    }
}