package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeNil : Type() {
    override val identifier = TypeIdentifier("Nil")
}

object ValueNil : Value {
    override val type get() = TypeNil
    override fun toString() = "nil"
}