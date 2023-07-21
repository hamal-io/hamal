package io.hamal.lib.kua.value

data class ExtensionValue(
    val name: String,
    val functions: List<NamedFunctionValue<*, *, *, *>>
) : Value
