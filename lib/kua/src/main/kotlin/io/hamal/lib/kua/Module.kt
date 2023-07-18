package io.hamal.lib.kua

import io.hamal.lib.kua.value.NamedFuncValue

data class Module(
    val name: String,
    val namedFuncs: List<NamedFuncValue>
)
