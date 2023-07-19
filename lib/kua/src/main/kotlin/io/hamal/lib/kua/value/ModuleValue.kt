package io.hamal.lib.kua.value

data class ModuleValue(
    val name: String,
    val namedFuncs: List<NamedFuncValue>
) : Value {
    override val type = Value.Type.Module
}
