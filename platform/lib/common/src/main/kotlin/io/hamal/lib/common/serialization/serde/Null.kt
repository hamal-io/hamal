package io.hamal.lib.common.serialization.serde


data object SerdeNull : SerdePrimitive<SerdeNull> {
    override val isNull get(): Boolean = true
    override fun asNull(): SerdeNull = this
    override fun deepCopy() = this
}