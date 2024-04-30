package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.type.TypeNil

object ValueNil : Value {
    override val type get() = TypeNil
    override fun toString() = "nil"
}