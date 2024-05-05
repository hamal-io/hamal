package io.hamal.lib.common.value

val TypeNil = ValueType("Nil")

object ValueNil : ValueSerializable {
    override val type get() = TypeNil
    override fun toString() = "nil"
}