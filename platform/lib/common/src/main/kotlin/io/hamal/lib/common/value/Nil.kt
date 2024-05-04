package io.hamal.lib.common.value

val TypeNil = ValueType("Nil")

object ValueNil : Value {
    override val type get() = TypeNil
    override fun toString() = "nil"
}