package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.ExtensionValue

interface SandboxFactory {
    fun create(): Sandbox
}

class Sandbox : AutoCloseable {
    private val bridge: Bridge = Bridge()
    val stack = Stack(bridge)

    fun register(extension: ExtensionValue) = bridge.registerExtension(extension)

    fun runCode(code: CodeValue) = runCode(code.value)

    fun runCode(code: String) = bridge.runCode(code)

    override fun close() {
//        state.close()
//        println("Implement me")
    }
}

internal fun Bridge.runCode(code: String) {
    loadString(code)
    call(0, 0)
}

internal fun Bridge.registerExtension(module: ExtensionValue) {
    val funcs = module.functions

    createTable(0, funcs.size)
    funcs.forEach { namedFunc ->
        pushFunctionValue(namedFunc.function)
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