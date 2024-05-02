package io.hamal.lib.common.serialization.json


data object JsonNull : JsonPrimitive<JsonNull> {
    override val isNull get(): Boolean = true
    override fun asNull(): JsonNull = this
    override fun deepCopy() = this
}