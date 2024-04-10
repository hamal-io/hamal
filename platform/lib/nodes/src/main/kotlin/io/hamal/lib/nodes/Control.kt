package io.hamal.lib.nodes

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import java.lang.reflect.Type

class ControlId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}


sealed interface Control {
    val id: ControlId
    val type: Type

    enum class Type {
        Condition,
        Input,
        Text
    }

    data class Condition(
        override val id: ControlId
    ) : Control {
        override val type: Type get() = Type.Condition
    }

    data class Input(
        override val id: ControlId
    ) : Control {
        override val type: Type get() = Type.Input
    }

    data class Text(
        override val id: ControlId,
        val text: String?,
        val placeholder: String?,
    ) : Control {
        override val type: Type get() = Type.Text
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
            val type = Control.Type.valueOf(json.asJsonObject.get("type").asString)

            return when (type) {
                Type.Condition -> context.deserialize(json, Condition::class.java)
                Type.Input -> context.deserialize(json, Input::class.java)
                Type.Text -> context.deserialize(json, Text::class.java)
            }
        }
    }
}