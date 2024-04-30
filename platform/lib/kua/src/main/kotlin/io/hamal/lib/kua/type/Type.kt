package io.hamal.lib.kua.type

import kotlin.reflect.KClass

interface KuaType {
}

fun KClass<out KuaType>.checkExpectedType(expected: KClass<out KuaType>) {
    check(this == expected) {
        "Expected type to be ${
            expected.java.simpleName.replace("Kua", "").lowercase()
        } but was ${this.java.simpleName.replace("Kua", "").lowercase()}"
    }
}