package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Kind

object ValueNil : Value {
    override val kind get() = Kind.Nil

    override fun toString() = "nil"
}