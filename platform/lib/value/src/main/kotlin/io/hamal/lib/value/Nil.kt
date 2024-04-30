package io.hamal.lib.value

import io.hamal.lib.value.type.TypeNil

object ValueNil : Value {
    override val type get() = TypeNil
    override fun toString() = "nil"
}