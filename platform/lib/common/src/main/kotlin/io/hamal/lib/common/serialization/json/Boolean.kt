package io.hamal.lib.common.serialization.json

@JvmInline
value class JsonBoolean(val value: Boolean) : JsonPrimitive<JsonBoolean> {
    override val isBoolean get() : Boolean = true

    override fun asBoolean(): JsonBoolean = this

    override fun asString(): JsonString = JsonString(stringValue)

    override val booleanValue get() : Boolean = value

    override val stringValue get() : kotlin.String = value.toString()

    override fun deepCopy() = this

    override fun toString(): kotlin.String = value.toString()
}
