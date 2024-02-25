package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.extend.RunnerRegistry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.TableProxyMap
import io.hamal.lib.kua.type.KuaString

class RequirePlugin(
    private val registry: RunnerRegistry
) : Function1In1Out<KuaString, TableProxyMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput1Schema(TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): TableProxyMap {
        ctx.setGlobal("_factory", registry.loadPluginFactory(arg1.value))
        ctx.load("_instance = _factory()")

        val result = ctx.getGlobalTableMap("_instance")

        ctx.unsetGlobal("_factory")
        ctx.unsetGlobal("_instance")
        ctx.unsetGlobal("plugin")

        return result
    }
}
