package io.hamal.lib.kua.table

import io.hamal.lib.kua.State
import io.hamal.lib.kua.type.Type
import kotlin.reflect.KClass

fun State.tableKeyType(idx: Int): KClass<out Type> {
    native.pushNil()
    native.tableNext(idx)
    val result = type(-2)
    native.pop(2)
    return result
}