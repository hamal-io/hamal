package io.hamal.lib.common.serialization.serde


@JvmInline
value class SerdeString(val value: kotlin.String) : SerdePrimitive<SerdeString> {

    override val isString get() : Boolean = true

    override fun asString(): SerdeString = this

    override val stringValue get() : kotlin.String = value

    override fun deepCopy() = this

    override fun toString(): kotlin.String = stringValue
}