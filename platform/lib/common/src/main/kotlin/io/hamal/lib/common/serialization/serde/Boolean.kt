package io.hamal.lib.common.serialization.serde

@JvmInline
value class SerdeBoolean(val value: Boolean) : SerdePrimitive<SerdeBoolean> {
    override val isBoolean get() : Boolean = true

    override fun asBoolean(): SerdeBoolean = this

    override fun asString(): SerdeString = SerdeString(stringValue)

    override val booleanValue get() : Boolean = value

    override val stringValue get() : kotlin.String = value.toString()

    override fun deepCopy() = this

    override fun toString(): kotlin.String = value.toString()
}
