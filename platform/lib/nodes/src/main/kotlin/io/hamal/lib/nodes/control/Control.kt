package io.hamal.lib.nodes.control

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.nodes.ControlTextArea
import io.hamal.lib.nodes.NodeId

// FIXME drop distinguishing between constant and input -- same thing and having a port connector is optional
// FIXME boolean as checkbox

class ControlId(override val value: SnowflakeId) : ValueObjectId() {
    constructor(value: Int) : this(SnowflakeId(value.toLong()))
    constructor(value: String) : this(SnowflakeId(value.toLong(16)))
}

enum class ControlType {
    Condition,

    ConstantBoolean,
    ConstantDecimal,
    ConstantString,

    Init,
    Invoke,
    InputBoolean,
    InputNumber,
    TextArea,

    String
}

// FIXME becomes sealed after migration
interface Control {
    val id: ControlId
    val type: ControlType
    val nodeId: NodeId

    object Adapter : JsonAdapter<Control> {

        override fun serialize(
            src: Control,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src)
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
                ControlType.Init -> context.deserialize(json, ControlInit::class.java)
                ControlType.Invoke -> context.deserialize(json, ControlInvoke::class.java)
                ControlType.InputBoolean -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType.InputNumber -> context.deserialize(json, ControlInputNumber::class.java)
                ControlType.TextArea -> context.deserialize(json, ControlTextArea::class.java)
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
                ControlType.Init -> context.deserialize(json, ControlInit::class.java)
                ControlType.Invoke -> context.deserialize(json, ControlInvoke::class.java)
                ControlType.InputBoolean -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType.InputNumber -> context.deserialize(json, ControlInputNumber::class.java)
                ControlType.TextArea -> context.deserialize(json, ControlTextArea::class.java)
                ControlType.String -> context.deserialize(json, ControlString::class.java)
            }
        }
    }
}