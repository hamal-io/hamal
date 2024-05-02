package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.serialization.SerializationModule

object SerdeModule : SerializationModule() {
    init {
        set(JsonArray::class, JsonAdapters.Array)
        set(JsonBoolean::class, JsonAdapters.Boolean)
        set(JsonNode::class, JsonAdapters.Node)
        set(JsonNumber::class, JsonAdapters.Number)
        set(JsonNull::class, JsonAdapters.Null)
        set(JsonObject::class, JsonAdapters.Object)
        set(JsonPrimitive::class, JsonAdapters.Primitive)
    }
}