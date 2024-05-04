package io.hamal.lib.kua.value

import io.hamal.lib.common.value.ValueType
import io.hamal.lib.common.value.Value
import kotlin.reflect.KClass

interface KuaValue : Value {
    override val type: ValueType
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
