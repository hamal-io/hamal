package io.hamal.lib.common.serialization

import com.google.gson.*
import io.hamal.lib.common.serialization.serde.*
import java.util.function.Consumer

object GsonTransform {

    fun fromNode(node: SerdeNode<*>): JsonElement {
        if (node.isObject) {
            return fromObject(node.asObject())
        } else if (node.isArray) {
            return fromArray(node.asArray())
        } else if (node.isPrimitive) {
            return fromTerminal(node.asPrimitive()) ?: JsonNull.INSTANCE
        }
        return JsonNull.INSTANCE
    }

    fun toNode(element: JsonElement): SerdeNode<*> {
        if (element.isJsonObject) {
            return toObject(element.asJsonObject)
        } else if (element.isJsonArray) {
            return toArray(element.asJsonArray)
        } else if (element.isJsonPrimitive) {
            return toTerminal(element.asJsonPrimitive)
        }
        return SerdeNull
    }

    private fun fromObject(obj: SerdeObject): JsonObject {
        val result = JsonObject()
        obj.nodes.forEach { entry -> result.add(entry.key, fromNode(entry.value)) }
        return result
    }

    private fun fromArray(array: SerdeArray): JsonArray {
        val result = JsonArray()
        array.nodes.forEach { node -> result.add(fromNode(node)) }
        return result
    }

    private fun fromTerminal(terminal: SerdePrimitive<*>): JsonPrimitive? {
        if (terminal.isString) {
            return JsonPrimitive(terminal.stringValue)
        } else if (terminal.isBoolean) {
            return JsonPrimitive(terminal.booleanValue)
        } else if (terminal.isNumber) {
            return JsonPrimitive(terminal.decimalValue.toBigDecimal())
        }
        return null
    }

    private fun toObject(obj: JsonObject): SerdeObject {
        val resultBuilder = SerdeObject.builder()

        obj.entrySet().forEach(Consumer<Map.Entry<String, JsonElement>> { entry: Map.Entry<String, JsonElement> ->
            val key = entry.key
            val value = entry.value
            resultBuilder.set(key, toNode(value))
        })

        return resultBuilder.build()
    }

    private fun toArray(array: JsonArray): SerdeArray {
        val resultBuilder = SerdeArray.builder()
        for (element in array) {
            resultBuilder.append(toNode(element))
        }
        return resultBuilder.build()
    }


    private fun toTerminal(primitive: JsonPrimitive): SerdePrimitive<*> {
        if (primitive.isString) {
            return SerdeString(primitive.asString)
        } else if (primitive.isBoolean) {
            return SerdeBoolean(primitive.asBoolean)
        } else if (primitive.isNumber) {
            return SerdeNumber(primitive.asBigDecimal)
        }
        return SerdeNull
    }

}