package io.hamal.lib.common.serialization.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.serialization.GsonTransform
import io.hamal.lib.common.serialization.JsonAdapter
import java.lang.reflect.Type

internal object JsonAdapters {

    object Array : JsonAdapter<JsonArray> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonArray {
            return GsonTransform.toNode(json).asArray()
        }

        override fun serialize(src: JsonArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }
    }

    object Boolean : JsonAdapter<JsonBoolean> {
        override fun serialize(src: JsonBoolean, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonBoolean {
            return GsonTransform.toNode(json).asBoolean()
        }
    }

    object Node : JsonAdapter<JsonNode<*>> {
        override fun serialize(src: JsonNode<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonNode<*> {
            return GsonTransform.toNode(json)
        }
    }

    object Null : JsonAdapter<JsonNull> {
        override fun serialize(src: JsonNull, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonNull {
            return GsonTransform.toNode(json).asNull()
        }
    }

    object Number : JsonAdapter<JsonNumber> {
        override fun serialize(src: JsonNumber, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonNumber {
            return GsonTransform.toNode(json).asNumber()
        }
    }

    object String : JsonAdapter<JsonString> {
        override fun serialize(src: JsonString, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonString {
            return GsonTransform.toNode(json).asString()
        }
    }

    object Object : JsonAdapter<JsonObject> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonObject {
            return GsonTransform.toNode(json).asObject()
        }

        override fun serialize(src: JsonObject, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }
    }

    object Primitive : JsonAdapter<JsonPrimitive<*>> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonPrimitive<*> {
            return GsonTransform.toNode(json).asPrimitive()
        }

        override fun serialize(src: JsonPrimitive<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return GsonTransform.fromNode(src)
        }
    }
}