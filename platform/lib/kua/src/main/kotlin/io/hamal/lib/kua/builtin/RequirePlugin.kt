package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.SandboxRegistry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.tableGet
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.common.value.ValueString

class RequirePlugin(
    private val registry: SandboxRegistry
) : Function1In1Out<ValueString, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput1Schema(KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): KuaTable {
        registry.pluginPush(arg1)
        return ctx.tableGet(-1)
    }
}
