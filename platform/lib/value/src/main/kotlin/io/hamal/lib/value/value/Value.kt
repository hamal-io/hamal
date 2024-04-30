package io.hamal.lib.value.value

import io.hamal.lib.value.type.Type

sealed interface Value {
    val type: Type
}