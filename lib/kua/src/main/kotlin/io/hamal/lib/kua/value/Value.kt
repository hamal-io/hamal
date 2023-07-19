package io.hamal.lib.kua.value

import kotlinx.serialization.Serializable

@Serializable
sealed interface Value {
    val type: Type

    enum class Type {
        Boolean,
        Code,
        Decimal,
        Error,
        Func,
        Module,
        Nil,
        Number,
        String,
        Table
    }
}

