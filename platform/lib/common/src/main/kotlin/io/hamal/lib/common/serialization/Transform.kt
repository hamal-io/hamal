package io.hamal.lib.common.serialization


import io.hamal.lib.common.serialization.json.*
import java.util.function.Consumer
import com.google.gson.JsonArray as GsonArray
import com.google.gson.JsonElement as GsonElement
import com.google.gson.JsonNull as GsonNull
import com.google.gson.JsonObject as GsonObject
import com.google.gson.JsonPrimitive as GsonPrimitive

object GsonTransform {

    fun fromNode(node: JsonNode<*>): GsonElement {
        if (node.isObject) {
            return fromObject(node.asObject())
        } else if (node.isArray) {
            return fromArray(node.asArray())
        } else if (node.isPrimitive) {
            return fromPrimitive(node.asPrimitive()) ?: GsonNull.INSTANCE
        }
        return GsonNull.INSTANCE
    }

    fun toNode(element: GsonElement): JsonNode<*> {
        if (element.isJsonObject) {
            return toObject(element.asJsonObject)
        } else if (element.isJsonArray) {
            return toArray(element.asJsonArray)
        } else if (element.isJsonPrimitive) {
            return toPrimitive(element.asJsonPrimitive)
        }
        return JsonNull
    }

    private fun fromObject(obj: JsonObject): GsonObject {
        val result = GsonObject()
        obj.nodes.forEach { entry -> result.add(entry.key, fromNode(entry.value)) }
        return result
    }

    private fun fromArray(array: JsonArray): GsonArray {
        val result = GsonArray()
        array.nodes.forEach { node -> result.add(fromNode(node)) }
        return result
    }

    private fun fromPrimitive(primitive: JsonPrimitive<*>): GsonPrimitive? {
        if (primitive.isString) {
            return GsonPrimitive(primitive.stringValue)
        } else if (primitive.isBoolean) {
            return GsonPrimitive(primitive.booleanValue)
        } else if (primitive.isNumber) {
            return GsonPrimitive(primitive.decimalValue.toBigDecimal())
        }
        return null
    }

    private fun toObject(obj: GsonObject): JsonObject {
        val resultBuilder = JsonObject.builder()

        obj.entrySet().forEach(Consumer<Map.Entry<String, GsonElement>> { entry: Map.Entry<String, GsonElement> ->
            val key = entry.key
            val value = entry.value
            resultBuilder.set(key, toNode(value))
        })

        return resultBuilder.build()
    }

    private fun toArray(array: GsonArray): JsonArray {
        val resultBuilder = JsonArray.builder()
        for (element in array) {
            resultBuilder.append(toNode(element))
        }
        return resultBuilder.build()
    }


    private fun toPrimitive(primitive: GsonPrimitive): JsonPrimitive<*> {
        if (primitive.isString) {
            return JsonString(primitive.asString)
        } else if (primitive.isBoolean) {
            return JsonBoolean(primitive.asBoolean)
        } else if (primitive.isNumber) {
            return JsonNumber(primitive.asBigDecimal)
        }
        return JsonNull
    }

}