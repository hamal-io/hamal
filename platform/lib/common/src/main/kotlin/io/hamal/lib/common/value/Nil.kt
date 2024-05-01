package io.hamal.lib.common.value

data object TypeNil : Type() {
    override val identifier = TypeIdentifier("Nil")
}

object ValueNil : Value {
    override val type get() = TypeNil
    override fun toString() = "nil"
}