package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.type.Type

sealed interface Value {
    val type: Type
}