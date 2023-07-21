package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.ExtensionValue

interface SandboxFactory {
    fun create(): Sandbox
}

class Sandbox : State, AutoCloseable {
    override val bridge: Bridge = Bridge()
    val state = ClosableState(bridge)

    fun register(extension: ExtensionValue) = bridge.registerExtension(extension)

    fun runCode(code: CodeValue) = runCode(code.value)

    fun runCode(code: String) = bridge.runCode(code)

    override fun close() {
//        state.close()
//        println("Implement me")
    }

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun length() = state.length()
    override fun setTop(idx: Int) = state.setTop(idx)

    override fun type(idx: Int) = state.type(idx)
    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)
}

internal fun Bridge.runCode(code: String) {
    loadString(code)
    call(0, 0)
}

internal fun Bridge.registerExtension(module: ExtensionValue) {
    val funcs = module.functions

    tableCreate(0, funcs.size)
    funcs.forEach { namedFunc ->
        pushFunctionValue(namedFunc.function)
        tabletSetField(1, namedFunc.name)
    }
//    if (global) {
//        rawGet(REGISTRYINDEX, LuaState.RIDX_GLOBALS)
    tableGetRawIdx(luaRegistryIndex(), 2)
//        pushValue(-2)
    push(-2)
    tabletSetField(-2, module.name)
    pop(1)
//    }

}