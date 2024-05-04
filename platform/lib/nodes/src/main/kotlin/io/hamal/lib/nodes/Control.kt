package io.hamal.lib.nodes

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.ControlType.Companion.ControlType

data class ControlCapture(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    override val port: PortInput,
) : ControlInput {
    override val type: ControlType = ControlType("Capture")
}

data class ControlCheckbox(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    override val port: PortInput,
    val value: ValueBoolean
) : ControlInput {
    override val type: ControlType = ControlType("Checkbox")
}

data class ControlCondition(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    override val port: PortInput,
) : ControlInput {
    override val type: ControlType = ControlType("Condition")
}

data class ControlInit(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    val config: Config = Config(),
    val description: String = "",
) : Control {
    override val type: ControlType = ControlType("Init")

    class Config(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

}

data class ControlInvoke(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    override val port: PortInput
) : ControlInput {
    override val type: ControlType = ControlType("Invoke")
}

data class ControlNumberInput(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    override val port: PortInput,
    val value: ValueNumber
) : ControlInput {
    override val type: ControlType = ControlType("Input_Number")
}

data class ControlTextArea(
    override val identifier: ControlIdentifier,
    override val nodeId: NodeId,
    override val port: PortInput,
    val value: ValueString
) : ControlInput {
    override val type: ControlType = ControlType("Text_Area")
}


// FIXME drop distinguishing between constant and input -- same thing and having a port connector is optional
// FIXME boolean as checkbox
class ControlIdentifier(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ControlIdentifier(value: String) = ControlIdentifier(ValueString(value))
    }
}

class ControlType(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ControlType(value: String) = ControlType(ValueString(value))
    }
}


// FIXME becomes sealed after migration
interface Control {
    val identifier: ControlIdentifier
    val type: ControlType
    val nodeId: NodeId

    object Adapter : AdapterJson<Control> {

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
            val type = ControlType(json.asJsonObject.get("type").asString)

            return when (type) {
                ControlType("Checkbox") -> context.deserialize(json, ControlCheckbox::class.java)
                ControlType("Capture") -> context.deserialize(json, ControlCapture::class.java)
                ControlType("Condition") -> context.deserialize(json, ControlCondition::class.java)
                ControlType("Init") -> context.deserialize(json, ControlInit::class.java)
                ControlType("Invoke") -> context.deserialize(json, ControlInvoke::class.java)
                ControlType("Input_Number") -> context.deserialize(json, ControlNumberInput::class.java)
                ControlType("Text_Area") -> context.deserialize(json, ControlTextArea::class.java)
                else -> TODO()
            }
        }
    }
}

interface ControlInput : Control {
    val port: PortInput
}

sealed interface TemplateControl {

    val type: ControlType

    object Adapter : AdapterJson<TemplateControl> {

        override fun serialize(
            src: TemplateControl,
            typeOfSrc: java.lang.reflect.Type,
            context: JsonSerializationContext
        ): JsonElement {
            TODO("Not yet implemented")
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: java.lang.reflect.Type,
            context: JsonDeserializationContext
        ): TemplateControl {
            val type = ControlType(json.asJsonObject.get("type").asString)

            return when (type) {
                ControlType("Checkbox") -> context.deserialize(json, ControlCheckbox::class.java)
                ControlType("Capture") -> context.deserialize(json, ControlCapture::class.java)
                ControlType("Condition") -> context.deserialize(json, ControlCondition::class.java)
                ControlType("Init") -> context.deserialize(json, ControlInit::class.java)
                ControlType("Invoke") -> context.deserialize(json, ControlInvoke::class.java)
                ControlType("Input_Number") -> context.deserialize(json, ControlNumberInput::class.java)
                ControlType("Text_Area") -> context.deserialize(json, ControlTextArea::class.java)
                else -> TODO()
            }
        }
    }
}