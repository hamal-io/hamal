package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue

class Sandbox(internal val state: State) {
    internal val stack = Stack(state)

    fun register(module: Module) = state.registerModule(module)

    fun runCode(code: CodeValue) = state.runCode(code.value)
}

internal fun State.runCode(code: String) {
    loadString(code)
    call(0, 0)
}

internal fun State.registerModule(module: Module) {
    val funcs = module.namedFuncs

    createTable(0, funcs.size)
    funcs.forEach { namedFunc ->
        pushFuncValue(namedFunc.func)
        setTableField(1, namedFunc.name)
    }
//    if (global) {
//        rawGet(REGISTRYINDEX, LuaState.RIDX_GLOBALS)
    getTableRawIdx(luaRegistryIndex(), 2)
//        pushValue(-2)
    push(-2)
    setTableField(-2, module.name)
    pop(1)
//    }

}