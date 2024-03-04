package io.hamal.lib.kua.type

import kotlin.reflect.KClass

sealed interface KuaType {
    enum class Type {
        Any,
        Boolean,
        Code,
        Decimal,
        Error,
        Function,
        Table,
        Nil,
        Number,
        String,
        Reference
    }

    val type: Type
}

fun KClass<out KuaType>.checkExpectedType(expected: KClass<out KuaType>) {
    check(this == expected) {
        "Expected type to be ${
            expected.java.simpleName.replace("Kua", "").lowercase()
        } but was ${this.java.simpleName.replace("Kua", "").lowercase()}"
    }
}