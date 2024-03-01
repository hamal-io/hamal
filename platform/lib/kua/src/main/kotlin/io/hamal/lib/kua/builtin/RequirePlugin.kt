package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.SandboxRegistry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable

class RequirePlugin(
    private val registry: SandboxRegistry
) : Function1In1Out<KuaString, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput1Schema(KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): KuaTable {
        ctx.globalSet(KuaString("_factory"), registry.loadPluginFactory(arg1.value))
        ctx.codeLoad(KuaCode("_instance = _factory"))

        val result = ctx.globalGetTable(KuaString("_instance"))

        ctx.globalUnset(KuaString("_factory"))
        ctx.globalUnset(KuaString("_instance"))
        ctx.globalUnset(KuaString("plugin"))

        return result
    }
}
