package io.hamal.lib.common.serialization.json


@JvmInline
value class JsonString(val value: String) : JsonPrimitive<JsonString> {

    override val isString get() : Boolean = true

    override fun asString(): JsonString = this

    override val stringValue get() : String = value

    override fun deepCopy() = this

    override fun toString(): String = stringValue
}