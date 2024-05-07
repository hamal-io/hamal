package io.hamal.lib.common.serialization.json

sealed interface JsonPrimitive<NODE_TYPE : JsonPrimitive<NODE_TYPE>> : JsonNode<NODE_TYPE> {
    override val isPrimitive get() : Boolean = true
    override fun asPrimitive(): JsonPrimitive<*> = this
}