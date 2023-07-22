package io.hamal.lib.kua

import io.hamal.lib.kua.function.NamedFunctionValue


data class Extension(
    val name: String,
    val functions: List<NamedFunctionValue<*, *, *, *>>
)
