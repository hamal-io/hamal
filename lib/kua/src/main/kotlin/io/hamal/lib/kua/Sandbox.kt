package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.ModuleValue

class Sandbox(loader: Loader) : AutoCloseable {
    private val state: Bridge = run {
        loader.load()
        Bridge()
    }
    val stack = Stack(state)

    fun register(module: ModuleValue) = state.registerModule(module)

    fun runCode(code: CodeValue) = runCode(code.value)

    fun runCode(code: String) = state.runCode(code)

    override fun close() {
//        state.close()
//        println("Implement me")
    }
}

internal fun Bridge.runCode(code: String) {
    loadString(code)
    call(0, 0)
}

internal fun Bridge.registerModule(module: ModuleValue) {
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