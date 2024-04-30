package io.hamal.lib.kua.type

import io.hamal.lib.value.Type
import io.hamal.lib.value.Value
import kotlin.reflect.KClass

interface KuaType : Value {
    override val type: Type
        get() = TODO("Not yet implemented")
}

fun KClass<out Value>.checkExpectedType(expected: KClass<out Value>) {
    check(this == expected) {
        "Expected type to be ${
            expected.java.simpleName
                .replace("Value", "")
                .replace("Kua", "")
                .lowercase()
        } but was ${
            this.java.simpleName
                .replace("Value", "")
                .replace("Kua", "")
                .lowercase()
        }"
    }
}
