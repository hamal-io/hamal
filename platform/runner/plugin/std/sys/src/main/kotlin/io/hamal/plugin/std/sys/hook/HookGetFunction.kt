package io.hamal.plugin.std.sys.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class HookGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.hook.get(HookId(arg1.value))
                .let { hook ->
                    ctx.tableCreate(
                        "id" to KuaString(hook.id.value.value.toString(16)),
                        "namespace" to ctx.tableCreate(
                            "id" to KuaString(hook.namespace.id.value.value.toString(16)),
                            "name" to KuaString(hook.namespace.name.value)
                        ),
                        "name" to KuaString(hook.name.value),
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}