package io.hamal.lib.nodes.control

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString
import io.hamal.lib.nodes.*
import io.hamal.lib.nodes.control.ControlType.Companion.ControlType

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
                ControlType("Test_Capture") -> context.deserialize(json, ControlCapture::class.java)
                ControlType("Condition") -> context.deserialize(json, ControlCondition::class.java)
                ControlType("ConstantBoolean") -> context.deserialize(json, ControlConstantBoolean::class.java)
                ControlType("ConstantDecimal") -> context.deserialize(json, ControlConstantDecimal::class.java)
                ControlType("ConstantString") -> context.deserialize(json, ControlConstantString::class.java)
                ControlType("Init") -> context.deserialize(json, ControlInit::class.java)
                ControlType("Invoke") -> context.deserialize(json, ControlInvoke::class.java)
                ControlType("InputBoolean") -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType("Input_Number") -> context.deserialize(json, ControlNumberInput::class.java)
                ControlType("Text_Area") -> context.deserialize(json, ControlTextArea::class.java)
                ControlType("String") -> context.deserialize(json, ControlString::class.java)
                else -> TODO()
            }
        }
    }
}

interface ControlInput : Control {
    val port: PortInput
}

sealed interface ControlExtension {

    val type: ControlType

    object Adapter : AdapterJson<ControlExtension> {

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
            val type = ControlType(json.asJsonObject.get("type").asString)

            return when (type) {
                ControlType("Checkbox") -> context.deserialize(json, ControlCheckbox::class.java)
                ControlType("Test_Capture") -> context.deserialize(json, ControlCapture::class.java)
                ControlType("Condition") -> context.deserialize(json, ControlCondition::class.java)
                ControlType("ConstantBoolean") -> context.deserialize(json, ControlConstantBoolean::class.java)
                ControlType("ConstantDecimal") -> context.deserialize(json, ControlConstantDecimal::class.java)
                ControlType("ConstantString") -> context.deserialize(json, ControlConstantString::class.java)
                ControlType("Init") -> context.deserialize(json, ControlInit::class.java)
                ControlType("Invoke") -> context.deserialize(json, ControlInvoke::class.java)
                ControlType("InputBoolean") -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType("Input_Number") -> context.deserialize(json, ControlNumberInput::class.java)
                ControlType("Text_Area") -> context.deserialize(json, ControlTextArea::class.java)
                ControlType("String") -> context.deserialize(json, ControlString::class.java)
                else -> TODO()
            }
        }
    }
}