package io.hamal.lib.nodes

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.AdapterJson
import io.hamal.lib.common.value.*
import io.hamal.lib.nodes.ControlType.Companion.ControlType
import java.util.*

data class ControlCode(
    override val index: ControlIndex,
    override val nodeIndex: NodeIndex,
    val value: ValueCode,
    override val key: ControlKey = ControlKey.random()
) : Control {
    override val type: ControlType = ControlType("Code")
}

data class ControlInputFromPort(
    override val index: ControlIndex,
    override val nodeIndex: NodeIndex,
    override val port: PortInput,
    override val key: ControlKey = ControlKey.random(),
) : ControlWithPort {
    override val type: ControlType = ControlType("Input_From_Port")
}

data class ControlInputBoolean(
    override val index: ControlIndex,
    override val nodeIndex: NodeIndex,
    override val port: PortInput?,
    val value: ValueBoolean,
    override val key: ControlKey = ControlKey.random(),
) : ControlWithPort {
    override val type: ControlType = ControlType("Input_Boolean")
}

data class ControlInputNumber(
    override val index: ControlIndex,
    override val nodeIndex: NodeIndex,
    override val port: PortInput?,
    val value: ValueNumber,
    override val key: ControlKey = ControlKey.random(),
) : ControlWithPort {
    override val type: ControlType = ControlType("Input_Number")
}

data class ControlInputString(
    override val index: ControlIndex,
    override val nodeIndex: NodeIndex,
    override val port: PortInput?,
    val value: ValueString,
    override val key: ControlKey = ControlKey.random(),
) : ControlWithPort {
    override val type: ControlType = ControlType("Input_String")
}


class ControlIndex(override val value: ValueNumber) : ValueVariableNumber() {
    companion object {
        fun ControlIndex(value: Int) = ControlIndex(ValueNumber(value))
    }

    override fun toString(): String = value.longValue.toString()
}

class ControlKey(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ControlKey(value: String) = ControlKey(ValueString(value))
        fun random() = ControlKey(UUID.randomUUID().toString())
    }
}


class ControlType(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun ControlType(value: String) = ControlType(ValueString(value))
    }
}


// FIXME becomes sealed after migration
interface Control {
    val index: ControlIndex
    val type: ControlType
    val nodeIndex: NodeIndex
    val key: ControlKey

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
                ControlType("Code") -> context.deserialize(json, ControlCode::class.java)
                ControlType("Input_Boolean") -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType("Input_From_Port") -> context.deserialize(json, ControlInputFromPort::class.java)
                ControlType("Input_Number") -> context.deserialize(json, ControlInputNumber::class.java)
                ControlType("Input_String") -> context.deserialize(json, ControlInputString::class.java)
                else -> TODO()
            }
        }
    }
}

interface ControlWithPort : Control {
    val port: PortInput?
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
                ControlType("Input_Boolean") -> context.deserialize(json, ControlInputBoolean::class.java)
                ControlType("Input_From_Port") -> context.deserialize(json, ControlInputFromPort::class.java)
                ControlType("Input_Number") -> context.deserialize(json, ControlInputNumber::class.java)
                ControlType("Input_String") -> context.deserialize(json, ControlInputString::class.java)
                else -> TODO()
            }
        }
    }
}