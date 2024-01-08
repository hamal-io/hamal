package io.hamal.lib.common.serialization

import com.google.gson.*
import io.hamal.lib.common.hot.*
import java.util.function.Consumer

object GsonTransform {

    fun fromNode(node: HotNode): JsonElement {
        if (node.isObject) {
            return fromObject(node.asObject())
        } else if (node.isArray) {
            return fromArray(node.asArray())
        } else if (node.isTerminal) {
            return fromTerminal(node.asTerminal()) ?: JsonNull.INSTANCE
        }
        return JsonNull.INSTANCE
    }

    fun toNode(element: JsonElement): HotNode {
        if (element.isJsonObject) {
            return toObject(element.asJsonObject)
        } else if (element.isJsonArray) {
            return toArray(element.asJsonArray)
        } else if (element.isJsonPrimitive) {
            return toTerminal(element.asJsonPrimitive)
        }
        return HotNull
    }

    private fun fromObject(obj: HotObject): JsonObject {
        val result = JsonObject()
        obj.nodes.forEach { entry -> result.add(entry.key, fromNode(entry.value)) }
        return result
    }

    private fun fromArray(array: HotArray): JsonArray {
        val result = JsonArray()
        array.nodes.forEach { node -> result.add(fromNode(node)) }
        return result
    }

    private fun fromTerminal(terminal: HotTerminal): JsonPrimitive? {
        if (terminal.isString) {
            return JsonPrimitive(terminal.stringValue)
        } else if (terminal.isBoolean) {
            return JsonPrimitive(terminal.booleanValue)
        } else if (terminal.isNumber) {
            return JsonPrimitive(terminal.bigDecimalValue)
        }
        return null
    }

    private fun toObject(obj: JsonObject): HotObject {
        val resultBuilder = HotObject.builder()

        obj.entrySet().forEach(Consumer<Map.Entry<String, JsonElement>> { entry: Map.Entry<String, JsonElement> ->
            val key = entry.key
            val value = entry.value
            resultBuilder.set(key, toNode(value))
        })

        return resultBuilder.build()
    }

    private fun toArray(array: JsonArray): HotArray {
        val resultBuilder = HotArray.builder()
        for (element in array) {
            resultBuilder.add(toNode(element))
        }
        return resultBuilder.build()
    }


    private fun toTerminal(primitive: JsonPrimitive): HotTerminal {
        if (primitive.isString) {
            return HotString(primitive.asString)
        } else if (primitive.isBoolean) {
            return HotBoolean(primitive.asBoolean)
        } else if (primitive.isNumber) {
            return HotNumber(primitive.asBigDecimal)
        }
        return HotNull
    }

}